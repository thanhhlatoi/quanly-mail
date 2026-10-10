package com.mycompany.myapp.service.Imlp;

import com.mycompany.myapp.domain.AdSenseChannelLink;
import com.mycompany.myapp.domain.EmailAccount;
import com.mycompany.myapp.domain.EmailAccountAssignment;
import com.mycompany.myapp.domain.EmailAccountStatus;
import com.mycompany.myapp.domain.EmailAccountServiceType;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.dto.request.EmailAccountRequest;
import com.mycompany.myapp.dto.response.EmailAccountImportResponse;
import com.mycompany.myapp.dto.response.EmailAccountResponse;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.repository.AdSenseChannelLinkRepository;
import com.mycompany.myapp.repository.AdSenseMonthlyRevenueRepository;
import com.mycompany.myapp.repository.AdSenseRevenueRepository;
import com.mycompany.myapp.repository.EmailAccountAssignmentRepository;
import com.mycompany.myapp.repository.EmailAccountRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.repository.YouTubeChannelRepository;
import com.mycompany.myapp.service.EmailAccountService;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmailAccountServiceImpl implements EmailAccountService {

    private final EmailAccountRepository emailAccountRepository;
    private final UserRepository userRepository;
    private final EmailAccountAssignmentRepository assignmentRepository;
    private final YouTubeChannelRepository channelRepository;
    private final AdSenseChannelLinkRepository linkRepository;
    private final AdSenseRevenueRepository revenueRepository;
    private final AdSenseMonthlyRevenueRepository monthlyRevenueRepository;
    private final com.mycompany.myapp.repository.EmailServiceRepository emailServiceRepository;

    public EmailAccountServiceImpl(
        EmailAccountRepository emailAccountRepository,
        UserRepository userRepository,
        EmailAccountAssignmentRepository assignmentRepository,
        YouTubeChannelRepository channelRepository,
        AdSenseChannelLinkRepository linkRepository,
        AdSenseRevenueRepository revenueRepository,
        AdSenseMonthlyRevenueRepository monthlyRevenueRepository
        , com.mycompany.myapp.repository.EmailServiceRepository emailServiceRepository
    ) {
        this.emailAccountRepository = emailAccountRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.channelRepository = channelRepository;
        this.linkRepository = linkRepository;
        this.revenueRepository = revenueRepository;
        this.monthlyRevenueRepository = monthlyRevenueRepository;
        this.emailServiceRepository = emailServiceRepository;
    }

    @Override
    public EmailAccountResponse create(EmailAccountRequest request) {
        if (
            request != null &&
            request.getEmailAddress() != null &&
            emailAccountRepository.existsByEmailAddressIgnoreCase(request.getEmailAddress())
        ) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email Gmail đã tồn tại trong kho");
        }
        return response(emailAccountRepository.save(toEntity(request)));
    }

    @Override
    @Transactional
    public List<EmailAccountResponse> createBulk(List<EmailAccountRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh sách email không được để trống");
        }

        List<EmailAccount> accounts = requests.stream().filter(Objects::nonNull).map(this::toEntity).collect(Collectors.toList());

        if (accounts.size() != requests.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh sách chứa phần tử email không hợp lệ");
        }

        return emailAccountRepository.saveAll(accounts).stream().map(this::response).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmailAccountImportResponse importCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File CSV không được để trống");
        }

        int imported = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            input.mark(8192);
            char separator = detectSeparator(input);
            input.reset();
            CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
            try (CSVReader reader = new CSVReaderBuilder(input).withCSVParser(parser).build()) {
                String[] headers = reader.readNext();
                if (headers == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File CSV không có dữ liệu");
                }

                Map<String, Integer> columns = columns(headers);
                Integer emailColumn = firstColumn(columns, "email", "username", "user", "email address", "emailaddress");
                Integer passwordColumn = firstColumn(columns, "password", "pass", "mat khau", "mật khẩu");
                if (emailColumn == null || passwordColumn == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV cần có tối thiểu hai cột Email và Password");
                }
                Integer recoveryEmailColumn = firstColumn(
                    columns,
                    "email khôi phục",
                    "recovery email",
                    "recoveryemail",
                    "mail recovery",
                    "mail khôi phục",
                    "email recovery"
                );
                Integer twoFactorColumn = firstColumn(columns, "mã 2fa", "2fa", "two factor", "twofactor", "two factor secret");
                Integer recoveryPhoneColumn = firstColumn(
                    columns,
                    "số điện thoại",
                    "recovery phone",
                    "recoveryphone",
                    "phone",
                    "phone recovery",
                    "số điện thoại recovery",
                    "phone khôi phục"
                );
                Integer countryColumn = firstColumn(columns, "quốc gia", "country", "country code", "countrycode");
                Integer yearColumn = firstColumn(columns, "year", "năm", "account year");

                int row = 1;
                String[] values;
                Set<String> importedEmails = new HashSet<>();
                while ((values = reader.readNext()) != null) {
                    row++;
                    if (values.length == 0 || Arrays.stream(values).allMatch(value -> value == null || value.isBlank())) {
                        continue;
                    }
                    String username = value(values, emailColumn);
                    String password = value(values, passwordColumn);
                    String recoveryEmail = value(values, recoveryEmailColumn);
                    String twoFactorSecret = value(values, twoFactorColumn);
                    String recoveryPhone = value(values, recoveryPhoneColumn);
                    String countryCode = countryCode(value(values, countryColumn));
                    Integer year = integerValue(values, yearColumn, row, errors);

                    if (username == null || password == null) {
                        skipped++;
                        errors.add("Dòng " + row + ": Username và Pass không được để trống");
                        continue;
                    }
                    if (
                        !importedEmails.add(username.toLowerCase(java.util.Locale.ROOT)) ||
                        emailAccountRepository.existsByEmailAddressIgnoreCase(username)
                    ) {
                        skipped++;
                        errors.add("Dòng " + row + ": email đã tồn tại " + username);
                        continue;
                    }

                    EmailAccount account = new EmailAccount();
                    account.setEmailAddress(username);
                    account.setPasswordEncrypted(password);
                    account.setRecoveryEmail(recoveryEmail);
                    account.setTwoFactorSecretEncrypted(twoFactorSecret);
                    account.setRecoveryPhone(recoveryPhone);
                    account.setCountryCode(countryCode);
                    account.setYear(year);
                    account.setStatus(EmailAccountStatus.ACTIVE);
                    emailAccountRepository.save(account);
                    imported++;
                }
            }
        } catch (IOException | CsvValidationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể đọc file CSV", exception);
        }

        return new EmailAccountImportResponse(imported, skipped, errors);
    }

    private Map<String, Integer> columns(String[] headers) {
        Map<String, Integer> columns = new HashMap<>();
        for (int index = 0; index < headers.length; index++) {
            columns.put(normalize(headers[index]), index);
        }
        return columns;
    }

    private char detectSeparator(BufferedReader input) throws IOException {
        char[] buffer = new char[8192];
        int length = input.read(buffer);
        if (length < 0) return ',';
        for (int index = 0; index < length; index++) {
            if (buffer[index] == '\t') return '\t';
        }
        return ',';
    }

    private Integer firstColumn(Map<String, Integer> columns, String... names) {
        return Arrays.stream(names).map(columns::get).filter(java.util.Objects::nonNull).findFirst().orElse(null);
    }

    private String value(String[] values, Integer index) {
        if (index == null || index >= values.length) {
            return null;
        }
        String value = values[index].trim().replaceAll("^\"|\"$", "").trim();
        return value.isBlank() ? null : value;
    }

    private Integer integerValue(String[] values, Integer index, int row, List<String> errors) {
        String value = value(values, index);
        if (value == null) return null;
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            errors.add("Dòng " + row + ": Year không phải số, đã bỏ trống");
            return null;
        }
    }

    private String normalize(String value) {
        return value.replace("\uFEFF", "").replace("\"", "").replaceAll("\\s+", " ").trim().toLowerCase(java.util.Locale.ROOT);
    }

    private String countryCode(String value) {
        if (value == null) return null;
        String normalized = value.trim().toLowerCase(java.util.Locale.ROOT);
        return switch (normalized) {
            case "united states", "united states of america", "usa", "us" -> "US";
            case "vietnam", "viet nam", "việt nam", "vn" -> "VN";
            case "united kingdom", "great britain", "uk", "gb" -> "GB";
            case "canada", "ca" -> "CA";
            case "australia", "au" -> "AU";
            default -> value.trim().length() <= 2
                ? value.trim().toUpperCase(java.util.Locale.ROOT)
                : value.trim().substring(0, 2).toUpperCase(java.util.Locale.ROOT);
        };
    }

    @Override
    public EmailAccountResponse update(String id, EmailAccountRequest request) {
        EmailAccount emailAccount = emailAccountRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản email"));
        ensureNotAdsenseAccount(id);
        if (request != null && request.getEmailAddress() != null) {
            emailAccountRepository
                .findByEmailAddressIgnoreCase(request.getEmailAddress())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email Gmail đã tồn tại trong kho");
                });
        }
        emailAccount.setEmailAddress(request.getEmailAddress());
        if (request.getPasswordEncrypted() != null && !request.getPasswordEncrypted().isBlank()) {
            emailAccount.setPasswordEncrypted(request.getPasswordEncrypted());
        }
        emailAccount.setTwoFactorSecretEncrypted(request.getTwoFactorSecretEncrypted());
        emailAccount.setRecoveryPhone(request.getRecoveryPhone());
        emailAccount.setRecoveryEmail(request.getRecoveryEmail());
        emailAccount.setCountryCode(countryCode(request.getCountryCode()));
        emailAccount.setYear(request.getYear());
        emailAccount.setPurchasePrice(request.getPurchasePrice());
        emailAccount.setNote(request.getNote());
        emailAccount.setStatus(request.getStatus() == null ? emailAccount.getStatus() : request.getStatus());
        emailAccount.setLastCheckedAt(request.getLastCheckedAt());
        emailAccount.setLastLoginAt(request.getLastLoginAt());

        emailAccount.setAssignedUser(findUser(request.getAssignedUser()));
        EmailAccount saveEmailAccount1 = emailAccountRepository.save(emailAccount);
        return response(saveEmailAccount1);
    }

    @Override
    public Optional<EmailAccountResponse> findById(String id) {
        return emailAccountRepository.findById(id).map(this::response);
    }

    @Override
    public List<EmailAccountResponse> findAll() {
        return emailAccountRepository.findAll().stream().map(this::response).collect(Collectors.toList());
    }

    @Override
    public List<EmailAccountResponse> findAvailableAccounts() {
        return findByStatus(EmailAccountStatus.ACTIVE);
    }

    @Override
    public List<EmailAccountResponse> findByStatus(EmailAccountStatus status) {
        return emailAccountRepository.findByStatus(status).stream().map(this::response).collect(Collectors.toList());
    }

    @Override
    public PageResponse<EmailAccountResponse> findPage(EmailAccountStatus status, Pageable pageable, boolean includeAdsense) {
        var page = includeAdsense
            ? status == null ? emailAccountRepository.findAll(pageable) : emailAccountRepository.findByStatus(status, pageable)
            : status == null ? emailAccountRepository.findAllVisibleInWarehouse(pageable) : emailAccountRepository.findVisibleInWarehouseByStatus(status, pageable);
        return PageResponse.from(page.map(this::response));
    }

    @Override
    public void changeStatus(String id, EmailAccountStatus status) {
        EmailAccount emailAccount = emailAccountRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản email"));
        ensureNotAdsenseAccount(id);
        emailAccount.setStatus(status);
        emailAccountRepository.save(emailAccount);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!emailAccountRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản email");
        }
        if (emailServiceRepository.existsByEmailAccountIdAndServiceType(id, EmailAccountServiceType.GOOGLE_ADSENSE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể xóa Gmail AdSense trong kho Gmail; hãy quản lý tại màn AdSense");
        }
        List<AdSenseChannelLink> links = linkRepository.findByAdsenseServiceEmailAccountId(id);
        List<Long> linkIds = links.stream().map(AdSenseChannelLink::getId).toList();
        if (!linkIds.isEmpty()) {
            monthlyRevenueRepository.deleteByLinkIdIn(linkIds);
            revenueRepository.deleteByLinkIdIn(linkIds);
            linkRepository.deleteAll(links);
        }
        List<EmailAccountAssignment> assignments = assignmentRepository.findByEmailAccountId(id);
        List<Long> assignmentIds = assignments.stream().map(EmailAccountAssignment::getId).toList();
        if (!assignmentIds.isEmpty()) {
            channelRepository.deleteAll(channelRepository.findByAssignmentIdIn(assignmentIds));
            assignmentRepository.deleteAll(assignments);
        }
        emailAccountRepository.deleteById(id);
    }

    private EmailAccountResponse response(EmailAccount emailAccount) {
        return new EmailAccountResponse(
            emailAccount.getId(),
            emailAccount.getEmailAddress(),
            emailAccount.getPasswordEncrypted(),
            emailAccount.getTwoFactorSecretEncrypted(),
            emailAccount.getRecoveryPhone(),
            emailAccount.getRecoveryEmail(),
            emailAccount.getCountryCode(),
            emailAccount.getYear(),
            emailAccount.getStatus(),
            emailAccount.getAssignedUser(),
            emailAccount.getPurchasePrice(),
            emailAccount.getNote(),
            emailAccount.getUpdatedAt(),
            emailAccount.getLastCheckedAt(),
            emailAccount.getLastLoginAt()
        );
    }

    private EmailAccount toEntity(EmailAccountRequest request) {
        if (request == null || request.getEmailAddress() == null || request.getEmailAddress().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address không được để trống");
        }
        if (request.getPasswordEncrypted() == null || request.getPasswordEncrypted().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu email không được để trống");
        }

        EmailAccount emailAccount = new EmailAccount();
        emailAccount.setEmailAddress(request.getEmailAddress());
        emailAccount.setPasswordEncrypted(request.getPasswordEncrypted());
        emailAccount.setTwoFactorSecretEncrypted(request.getTwoFactorSecretEncrypted());
        emailAccount.setRecoveryPhone(request.getRecoveryPhone());
        emailAccount.setRecoveryEmail(request.getRecoveryEmail());
        emailAccount.setCountryCode(countryCode(request.getCountryCode()));
        emailAccount.setYear(request.getYear());
        emailAccount.setPurchasePrice(request.getPurchasePrice());
        emailAccount.setNote(request.getNote());
        emailAccount.setStatus(request.getStatus() == null ? EmailAccountStatus.ACTIVE : request.getStatus());
        emailAccount.setLastCheckedAt(request.getLastCheckedAt());
        emailAccount.setLastLoginAt(request.getLastLoginAt());
        emailAccount.setAssignedUser(findUser(request.getAssignedUser()));
        return emailAccount;
    }

    private User findUser(Long userId) {
        return userId == null
            ? null
            : userRepository
                  .findById(userId)
                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng được gán"));
    }

    private void ensureNotAdsenseAccount(String id) {
        if (emailServiceRepository.existsByEmailAccountIdAndServiceType(id, EmailAccountServiceType.GOOGLE_ADSENSE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Gmail AdSense chỉ được quản lý tại màn Google AdSense");
        }
    }
}

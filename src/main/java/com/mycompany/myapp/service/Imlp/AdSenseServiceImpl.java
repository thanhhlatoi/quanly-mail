package com.mycompany.myapp.service.Imlp;

import com.mycompany.myapp.domain.AdSenseChannelLink;
import com.mycompany.myapp.domain.AdSenseMonthlyRevenue;
import com.mycompany.myapp.domain.AdSenseRevenue;
import com.mycompany.myapp.domain.EmailAccountServiceType;
import com.mycompany.myapp.domain.EmailService;
import com.mycompany.myapp.domain.MonetizationApplicationStatus;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.dto.request.AdSenseChannelLinkRequest;
import com.mycompany.myapp.dto.request.AdSenseMonthlyRevenueRequest;
import com.mycompany.myapp.dto.request.AdSenseRevenueRequest;
import com.mycompany.myapp.dto.response.AdSenseChannelLinkResponse;
import com.mycompany.myapp.dto.response.AdSenseMonthlyRevenueResponse;
import com.mycompany.myapp.dto.response.AdSenseMonthlyTotalResponse;
import com.mycompany.myapp.dto.response.AdSenseRevenueResponse;
import com.mycompany.myapp.dto.response.AdSenseRevenueSummaryResponse;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.repository.AdSenseChannelLinkRepository;
import com.mycompany.myapp.repository.AdSenseMonthlyRevenueRepository;
import com.mycompany.myapp.repository.AdSenseRevenueRepository;
import com.mycompany.myapp.repository.EmailServiceRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.repository.YouTubeChannelRepository;
import com.mycompany.myapp.repository.YouTubeMonetizationApplicationRepository;
import com.mycompany.myapp.service.AdSenseService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdSenseServiceImpl implements AdSenseService {

    private final EmailServiceRepository emailServiceRepository;
    private final YouTubeChannelRepository channelRepository;
    private final AdSenseChannelLinkRepository linkRepository;
    private final AdSenseRevenueRepository revenueRepository;
    private final UserRepository userRepository;
    private final AdSenseMonthlyRevenueRepository monthlyRevenueRepository;
    private final YouTubeMonetizationApplicationRepository monetizationRepository;

    public AdSenseServiceImpl(
        EmailServiceRepository emailServiceRepository,
        YouTubeChannelRepository channelRepository,
        AdSenseChannelLinkRepository linkRepository,
        AdSenseRevenueRepository revenueRepository,
        UserRepository userRepository,
        AdSenseMonthlyRevenueRepository monthlyRevenueRepository,
        YouTubeMonetizationApplicationRepository monetizationRepository
    ) {
        this.emailServiceRepository = emailServiceRepository;
        this.channelRepository = channelRepository;
        this.linkRepository = linkRepository;
        this.revenueRepository = revenueRepository;
        this.userRepository = userRepository;
        this.monthlyRevenueRepository = monthlyRevenueRepository;
        this.monetizationRepository = monetizationRepository;
    }

    @Override
    @Transactional
    public AdSenseChannelLinkResponse linkChannel(AdSenseChannelLinkRequest request) {
        EmailService adsenseService = emailServiceRepository
            .findById(request.getAdsenseServiceId())
            .orElseThrow(() -> notFound("Không tìm thấy dịch vụ Gmail AdSense"));
        if (adsenseService.getServiceType() != EmailAccountServiceType.GOOGLE_ADSENSE || !adsenseService.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gmail chưa được khai báo là AdSense đang hoạt động");
        }
        var channel = channelRepository.findDetailedById(request.getChannelId()).orElseThrow(() -> notFound("Không tìm thấy kênh YouTube"));

        AdSenseChannelLink link = linkRepository.findByChannelId(channel.getId()).orElseGet(AdSenseChannelLink::new);
        if (link.isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Kênh YouTube đã được nối với Gmail AdSense");
        }
        link.setAdsenseService(adsenseService);
        link.setChannel(channel);
        link.setActive(true);
        link.setNote(request.getNote());
        link.setCreatedAt(Instant.now());
        return toLinkResponse(linkRepository.save(link));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdSenseChannelLinkResponse> findLinks(Pageable pageable) {
        return PageResponse.from(linkRepository.findAll(pageable).map(this::toLinkResponse));
    }

    @Override
    @Transactional
    public void unlinkChannel(Long linkId) {
        AdSenseChannelLink link = linkRepository.findById(linkId).orElseThrow(() -> notFound("Không tìm thấy liên kết AdSense"));
        link.setActive(false);
        linkRepository.save(link);
    }

    @Override
    @Transactional
    public AdSenseRevenueResponse saveRevenue(Long linkId, AdSenseRevenueRequest request, Long adminId) {
        AdSenseChannelLink link = linkRepository.findDetailedById(linkId).orElseThrow(() -> notFound("Không tìm thấy liên kết AdSense"));
        if (!link.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Liên kết AdSense đã bị tắt");
        }
        User admin = userRepository.findById(adminId).orElseThrow(() -> notFound("Không tìm thấy admin"));
        AdSenseRevenue revenue = revenueRepository
            .findByLinkIdAndRevenueDate(linkId, request.getRevenueDate())
            .orElseGet(AdSenseRevenue::new);
        revenue.setLink(link);
        revenue.setRevenueDate(request.getRevenueDate());
        revenue.setAmount(request.getAmount());
        revenue.setCurrency(request.getCurrency() == null || request.getCurrency().isBlank() ? "USD" : request.getCurrency().toUpperCase());
        revenue.setNote(request.getNote());
        revenue.setUpdatedBy(admin);
        revenue.setUpdatedAt(Instant.now());
        return toRevenueResponse(revenueRepository.save(revenue));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdSenseRevenueResponse> findRevenues(Pageable pageable) {
        return PageResponse.from(revenueRepository.findAll(pageable).map(this::toRevenueResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public AdSenseRevenueSummaryResponse summarize(LocalDate from, LocalDate to, String currency) {
        String normalizedCurrency = currency == null || currency.isBlank() ? "USD" : currency.toUpperCase();
        return new AdSenseRevenueSummaryResponse(from, to, revenueRepository.sumAmount(from, to, normalizedCurrency), normalizedCurrency);
    }

    @Override
    @Transactional
    public AdSenseMonthlyRevenueResponse saveMonthlyRevenue(Long channelId, AdSenseMonthlyRevenueRequest request, Long userId) {
        YearMonth.parse(request.getRevenueMonth());
        var channel = channelRepository.findDetailedById(channelId).orElseThrow(() -> notFound("Không tìm thấy kênh YouTube"));
        if (!channel.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Kênh không thuộc user hiện tại");
        }
        if (!monetizationRepository.existsByChannelIdAndStatus(channelId, MonetizationApplicationStatus.APPROVED_PARTNER)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kênh chưa được duyệt bật kiếm tiền");
        }
        AdSenseChannelLink link = linkRepository
            .findByChannelId(channelId)
            .orElseThrow(() -> notFound("Kênh chưa được nối với Gmail AdSense"));
        if (!link.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Liên kết AdSense đã bị tắt");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> notFound("Không tìm thấy user"));
        AdSenseMonthlyRevenue revenue = monthlyRevenueRepository
            .findByLinkIdAndRevenueMonth(link.getId(), request.getRevenueMonth())
            .orElseGet(AdSenseMonthlyRevenue::new);
        revenue.setLink(link);
        revenue.setRevenueMonth(request.getRevenueMonth());
        revenue.setAmount(request.getAmount());
        revenue.setCurrency(request.getCurrency() == null || request.getCurrency().isBlank() ? "USD" : request.getCurrency().toUpperCase());
        revenue.setNote(request.getNote());
        revenue.setUpdatedBy(user);
        revenue.setUpdatedAt(Instant.now());
        return toMonthlyResponse(monthlyRevenueRepository.save(revenue));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdSenseMonthlyRevenueResponse> findMonthlyRevenues(Long userId, Pageable pageable) {
        var page =
            userId == null
                ? monthlyRevenueRepository.findAll(pageable)
                : monthlyRevenueRepository.findByLinkChannelUserId(userId, pageable);
        return PageResponse.from(page.map(this::toMonthlyResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdSenseMonthlyTotalResponse> monthlyTotals(String month, String currency) {
        YearMonth.parse(month);
        return monthlyRevenueRepository.totalsByService(month, currency == null || currency.isBlank() ? "USD" : currency.toUpperCase());
    }

    private AdSenseChannelLinkResponse toLinkResponse(AdSenseChannelLink link) {
        return new AdSenseChannelLinkResponse(
            link.getId(),
            link.getAdsenseService().getId(),
            link.getAdsenseService().getEmailAccount().getEmailAddress(),
            link.getChannel().getId(),
            link.getChannel().getChannelUrl(),
            link.getChannel().getChannelName(),
            link.getChannel().getUser().getId(),
            link.getChannel().getUser().getEmail(),
            link.isActive(),
            link.getNote(),
            link.getCreatedAt()
        );
    }

    private AdSenseRevenueResponse toRevenueResponse(AdSenseRevenue revenue) {
        return new AdSenseRevenueResponse(
            revenue.getId(),
            revenue.getLink().getId(),
            revenue.getLink().getAdsenseService().getEmailAccount().getEmailAddress(),
            revenue.getLink().getChannel().getId(),
            revenue.getLink().getChannel().getChannelUrl(),
            revenue.getLink().getChannel().getUser().getId(),
            revenue.getLink().getChannel().getUser().getEmail(),
            revenue.getRevenueDate(),
            revenue.getAmount(),
            revenue.getCurrency(),
            revenue.getNote(),
            revenue.getUpdatedBy().getId(),
            revenue.getUpdatedAt()
        );
    }

    private AdSenseMonthlyRevenueResponse toMonthlyResponse(AdSenseMonthlyRevenue revenue) {
        return new AdSenseMonthlyRevenueResponse(
            revenue.getId(),
            revenue.getLink().getId(),
            revenue.getLink().getAdsenseService().getId(),
            revenue.getLink().getAdsenseService().getEmailAccount().getEmailAddress(),
            revenue.getLink().getChannel().getId(),
            revenue.getLink().getChannel().getChannelUrl(),
            revenue.getLink().getChannel().getChannelName(),
            revenue.getLink().getChannel().getUser().getId(),
            revenue.getLink().getChannel().getUser().getEmail(),
            revenue.getRevenueMonth(),
            revenue.getAmount(),
            revenue.getCurrency(),
            revenue.getNote(),
            revenue.getUpdatedBy().getId()
        );
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}

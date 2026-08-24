package com.mycompany.myapp.service.Imlp;

import static com.mycompany.myapp.security.SecurityUtils.JWT_ALGORITHM;

import com.mycompany.myapp.config.ApplicationProperties;
import com.mycompany.myapp.domain.Role;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.dto.request.GoogleLoginRequest;
import com.mycompany.myapp.dto.response.LoginResponse;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.GoogleLoginService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GoogleLoginServiceImpl implements GoogleLoginService {

    private final UserRepository userRepository;
    private final JwtEncoder jwtEncoder;
    private final RestClient restClient;
    private final ApplicationProperties.Google googleProperties;

    public GoogleLoginServiceImpl(
        UserRepository userRepository,
        JwtEncoder jwtEncoder,
        RestClient.Builder restClientBuilder,
        ApplicationProperties applicationProperties
    ) {
        this.userRepository = userRepository;
        this.jwtEncoder = jwtEncoder;
        this.restClient = restClientBuilder.build();
        this.googleProperties = applicationProperties.getSecurity().getGoogle();
    }

    @Override
    public LoginResponse login(GoogleLoginRequest request) {
        Map<String, Object> googleUser = verifyGoogleIdToken(request.getIdToken());
        String email = stringValue(googleUser.get("email"));
        boolean emailVerified = booleanValue(googleUser.get("email_verified")) || booleanValue(googleUser.get("verified_email"));
        if (email == null || !emailVerified) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản Google chưa xác minh email");
        }

        User user = userRepository.findByEmailIgnoreCase(email).orElseGet(() -> createPendingUser(email, googleUser));
        if (!user.isStatus()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản đang chờ admin xét duyệt");
        }

        List<String> authorities = user
            .getRoles()
            .stream()
            .map(Role::getName)
            .filter(name -> name != null && !name.isBlank())
            .map(this::toAuthority)
            .collect(Collectors.toList());
        String token = createToken(user, email, authorities);
        return new LoginResponse(token, user.getId(), email, authorities);
    }

    private User createPendingUser(String email, Map<String, Object> googleUser) {
        User user = new User();
        user.setEmail(email);
        user.setUserName(email);
        user.setFullName(stringValue(googleUser.get("name")) != null ? stringValue(googleUser.get("name")) : email);
        user.setDateOfBirth(LocalDate.of(1970, 1, 1));
        user.setStatus(false);
        return userRepository.save(user);
    }

    private Map<String, Object> verifyGoogleIdToken(String idToken) {
        String googleClientId = googleProperties.getClientId();
        if (googleClientId == null || googleClientId.isBlank()) {
            throw new IllegalStateException("Chưa cấu hình application.security.google.client-id");
        }
        try {
            Map<String, Object> response = restClient
                .get()
                .uri(uriBuilder ->
                    uriBuilder.scheme("https").host("oauth2.googleapis.com").path("/tokeninfo").queryParam("id_token", idToken).build()
                )
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            String audience = stringValue(response == null ? null : response.get("aud"));
            if (response == null || !googleClientId.equals(audience)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google ID token không hợp lệ");
            }
            return response;
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không thể xác thực Google ID token", exception);
        }
    }

    private String createToken(User user, String email, List<String> authorities) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("quanly-email")
            .issuedAt(now)
            .expiresAt(now.plus(24, ChronoUnit.HOURS))
            .subject(email)
            .claim("userId", user.getId())
            .claim("auth", authorities)
            .build();
        JwsHeader header = JwsHeader.with(JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private String toAuthority(String roleName) {
        String normalized = roleName.trim().toUpperCase(Locale.ROOT);
        return normalized.startsWith("ROLE_") ? normalized : "ROLE_" + normalized;
    }

    private String stringValue(Object value) {
        if (value == null) {
            return null;
        }
        String result = String.valueOf(value).trim();
        return result.isBlank() ? null : result;
    }

    private boolean booleanValue(Object value) {
        return value instanceof Boolean booleanValue ? booleanValue : value != null && Boolean.parseBoolean(String.valueOf(value));
    }
}

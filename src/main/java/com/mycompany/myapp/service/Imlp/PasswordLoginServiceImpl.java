package com.mycompany.myapp.service.Imlp;

import static com.mycompany.myapp.security.SecurityUtils.JWT_ALGORITHM;

import com.mycompany.myapp.domain.Role;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.dto.request.LoginRequest;
import com.mycompany.myapp.dto.response.LoginResponse;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.PasswordLoginService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PasswordLoginServiceImpl implements PasswordLoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public PasswordLoginServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String userName = request.getUserName().trim();
        User user = userRepository
            .findByUserNameIgnoreCase(userName)
            .filter(User::isStatus)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tên đăng nhập hoặc mật khẩu không đúng"));

        if ("GOOGLE_LOGIN_ONLY".equals(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản này cần đăng nhập bằng Google");
        }

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tên đăng nhập hoặc mật khẩu không đúng");
        }

        List<String> authorities = user
            .getRoles()
            .stream()
            .map(Role::getName)
            .filter(name -> name != null && !name.isBlank())
            .map(this::toAuthority)
            .collect(Collectors.toList());
        String token = createToken(user, authorities);
        return new LoginResponse(token, user.getId(), user.getEmail(), authorities);
    }

    private String createToken(User user, List<String> authorities) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("quanly-email")
            .issuedAt(now)
            .expiresAt(now.plus(24, ChronoUnit.HOURS))
            .subject(user.getUserName())
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
}

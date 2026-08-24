package com.mycompany.myapp.controller;

import com.mycompany.myapp.dto.response.AuthMeResponse;
import com.mycompany.myapp.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthMeResponse> me(Authentication authentication) {
        String accessToken = SecurityUtils.getCurrentUserJWT().orElseThrow(() ->
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa đăng nhập")
        );

        boolean isAdmin = authentication
            .getAuthorities()
            .stream()
            .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        return ResponseEntity.ok(new AuthMeResponse(accessToken, isAdmin ? "admin" : "user"));
    }
}

package com.mycompany.myapp.controller;

import com.mycompany.myapp.dto.request.GoogleLoginRequest;
import com.mycompany.myapp.dto.request.LoginRequest;
import com.mycompany.myapp.dto.response.LoginResponse;
import com.mycompany.myapp.service.GoogleLoginService;
import com.mycompany.myapp.service.PasswordLoginService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authenticate")
public class GoogleLoginController {

    private final GoogleLoginService googleLoginService;
    private final PasswordLoginService passwordLoginService;

    public GoogleLoginController(GoogleLoginService googleLoginService, PasswordLoginService passwordLoginService) {
        this.googleLoginService = googleLoginService;
        this.passwordLoginService = passwordLoginService;
    }

    @PostMapping
    public ResponseEntity<LoginResponse> loginWithPassword(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(passwordLoginService.login(request));
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody GoogleLoginRequest request) {
        return ResponseEntity.ok(googleLoginService.login(request));
    }
}

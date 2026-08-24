package com.mycompany.myapp.service;

import com.mycompany.myapp.dto.request.LoginRequest;
import com.mycompany.myapp.dto.response.LoginResponse;

public interface PasswordLoginService {
    LoginResponse login(LoginRequest request);
}

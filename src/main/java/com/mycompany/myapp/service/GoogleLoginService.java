package com.mycompany.myapp.service;

import com.mycompany.myapp.dto.request.GoogleLoginRequest;
import com.mycompany.myapp.dto.response.LoginResponse;

public interface GoogleLoginService {
    LoginResponse login(GoogleLoginRequest request);
}

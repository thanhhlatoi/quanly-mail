package com.mycompany.myapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @JsonAlias({ "username", "user_name", "login" })
    private String userName;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}

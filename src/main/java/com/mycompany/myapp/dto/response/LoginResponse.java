package com.mycompany.myapp.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String idToken;
    private Long userId;
    private String email;
    private List<String> authorities;
}

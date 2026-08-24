package com.mycompany.myapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private Long recipientId;
}

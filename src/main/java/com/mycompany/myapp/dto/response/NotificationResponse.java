package com.mycompany.myapp.dto.response;

import java.time.Instant;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private String title;
    private String content;
    private Long recipientId;
    private String recipientName;
    private boolean read;
    private Instant createdAt;
}

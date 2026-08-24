package com.mycompany.myapp.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdSenseRevenueResponse {

    private Long id;
    private Long linkId;
    private String adsenseEmail;
    private Long channelId;
    private String channelUrl;
    private Long userId;
    private String userEmail;
    private LocalDate revenueDate;
    private BigDecimal amount;
    private String currency;
    private String note;
    private Long updatedBy;
    private Instant updatedAt;
}

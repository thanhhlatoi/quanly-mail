package com.mycompany.myapp.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdSenseMonthlyRevenueResponse {

    private Long id;
    private Long linkId;
    private Long adsenseServiceId;
    private String adsenseEmail;
    private Long channelId;
    private String channelUrl;
    private String channelName;
    private Long userId;
    private String userEmail;
    private String revenueMonth;
    private BigDecimal amount;
    private String currency;
    private String note;
    private Long updatedBy;
}

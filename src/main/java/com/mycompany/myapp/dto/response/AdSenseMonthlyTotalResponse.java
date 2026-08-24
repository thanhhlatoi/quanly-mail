package com.mycompany.myapp.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdSenseMonthlyTotalResponse {

    private String revenueMonth;
    private Long adsenseServiceId;
    private String adsenseEmail;
    private BigDecimal totalAmount;
    private String currency;
}

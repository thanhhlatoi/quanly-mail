package com.mycompany.myapp.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdSenseRevenueSummaryResponse {

    private LocalDate from;
    private LocalDate to;
    private BigDecimal totalAmount;
    private String currency;
}

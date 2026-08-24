package com.mycompany.myapp.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdSenseMonthlyRevenueRequest {

    @NotBlank
    private String revenueMonth;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal amount;

    private String currency;
    private String note;
}

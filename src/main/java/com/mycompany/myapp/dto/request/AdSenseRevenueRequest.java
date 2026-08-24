package com.mycompany.myapp.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdSenseRevenueRequest {

    @NotNull
    private LocalDate revenueDate;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal amount;

    private String currency;
    private String note;
}

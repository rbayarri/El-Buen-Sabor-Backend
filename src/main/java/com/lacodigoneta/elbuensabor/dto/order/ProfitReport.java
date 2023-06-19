package com.lacodigoneta.elbuensabor.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfitReport {

    private BigDecimal profits;

    private BigDecimal costs;

    private BigDecimal holdingResults;
}

package com.lacodigoneta.elbuensabor.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RankingClients {

    private UUID id;

    private String name;

    private String lastName;

    private BigDecimal total;

    private int quantity;
}

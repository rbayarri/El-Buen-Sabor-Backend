package com.lacodigoneta.elbuensabor.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RankingProduct {

    private String productName;

    private int quantity;

    private int cookingTime;

}

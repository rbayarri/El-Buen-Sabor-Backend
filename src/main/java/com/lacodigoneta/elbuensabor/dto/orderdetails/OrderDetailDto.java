package com.lacodigoneta.elbuensabor.dto.orderdetails;

import com.lacodigoneta.elbuensabor.dto.product.ProductForOrderDetailDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailDto {

    private ProductForOrderDetailDto product;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal unitCost;

    private BigDecimal discount;
}

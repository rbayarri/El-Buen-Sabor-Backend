package com.lacodigoneta.elbuensabor.dto.product;

import com.lacodigoneta.elbuensabor.dto.ImageDto;
import com.lacodigoneta.elbuensabor.dto.productdetails.ProductDetailDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductForOrderDetailDto {

    private UUID id;

    private String name;

    private String description;

    private ImageDto image;

    private String recipe;

    private List<ProductDetailDto> productDetails;

}

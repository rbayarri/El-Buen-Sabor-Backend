package com.lacodigoneta.elbuensabor.dto.category;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryWithSubcategoriesDto {

    private UUID id;

    private String name;

    private boolean active;

    private boolean container;

    private List<CategoryWithSubcategoriesDto> subCategories;

}

package com.lacodigoneta.elbuensabor.entities;

import com.lacodigoneta.elbuensabor.enums.MeasurementUnit;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ingredients")
public class Ingredient extends BaseEntity {

    private String name;

    @ManyToOne(optional = false)
    private Category category;

    @Enumerated(value = EnumType.STRING)
    private MeasurementUnit measurementUnit;

    @Transient
    private BigDecimal currentStock;

    @Column(columnDefinition = "decimal(38,10)")
    private BigDecimal minimumStock;

    @Transient
    private BigDecimal lastCost;

    private boolean active;

    @OneToMany(mappedBy = "ingredient", orphanRemoval = true)
    private List<IngredientPurchase> ingredientPurchaseList;

    @OneToMany(mappedBy = "ingredient", orphanRemoval = true)
    private List<ProductDetail> productDetails;
}

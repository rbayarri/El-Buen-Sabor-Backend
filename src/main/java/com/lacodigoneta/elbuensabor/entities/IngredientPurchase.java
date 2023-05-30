package com.lacodigoneta.elbuensabor.entities;

import com.lacodigoneta.elbuensabor.enums.MeasurementUnit;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ingredient_purchases")
public class IngredientPurchase extends BaseEntity {

    private LocalDateTime dateTime;

    @ManyToOne(optional = false)
    private Ingredient ingredient;

    @Enumerated(EnumType.STRING)
    private MeasurementUnit clientMeasurementUnit;

    @Column(columnDefinition = "decimal(38,10)")
    private BigDecimal quantity;

    @Column(columnDefinition = "decimal(38,10)")
    private BigDecimal unitPrice;

}

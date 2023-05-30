package com.lacodigoneta.elbuensabor.entities;

import com.lacodigoneta.elbuensabor.enums.MeasurementUnit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "product_details")
public class ProductDetail extends BaseEntity {

    @ManyToOne(optional = false)
    private Product product;

    @ManyToOne(optional = false)
    private Ingredient ingredient;

    @Enumerated(EnumType.STRING)
    private MeasurementUnit clientMeasurementUnit;

    @Column(columnDefinition = "decimal(38,10)")
    private BigDecimal quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductDetail that = (ProductDetail) o;
        return ingredient.equals(that.ingredient) && clientMeasurementUnit.equals(that.clientMeasurementUnit) && quantity.compareTo(that.quantity) == 0;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

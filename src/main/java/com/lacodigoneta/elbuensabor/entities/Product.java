package com.lacodigoneta.elbuensabor.entities;

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
@Table(name = "products")
public class Product extends BaseEntity {

    private String name;

    private String description;

    @ManyToOne(optional = false)
    private Category category;

    @ManyToOne
    private Image image;

    private Integer cookingTime;

    @Column(columnDefinition = "TEXT")
    private String recipe;

    @Transient
    private BigDecimal price;

    private BigDecimal profitMargin;

    private boolean active;

    @Transient
    private Integer stock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetail> productDetails;

    @OneToMany(mappedBy = "product")
    private List<OrderDetail> orderDetails;

}

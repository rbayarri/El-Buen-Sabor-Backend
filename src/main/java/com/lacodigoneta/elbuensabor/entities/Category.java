package com.lacodigoneta.elbuensabor.entities;

import com.lacodigoneta.elbuensabor.enums.CategoryType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "categories")
public class Category extends BaseEntity {

    private String name;

    @Enumerated(value = EnumType.STRING)
    private CategoryType type;

    private boolean container;

    private boolean active;

    @ManyToOne
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Category> subCategories;

    @OneToMany(mappedBy = "category", orphanRemoval = true)
    private List<Ingredient> ingredientChildren;

    @OneToMany(mappedBy = "category", orphanRemoval = true)
    private List<Product> productChildren;

}

package com.lacodigoneta.elbuensabor.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "addresses")
public class Address extends BaseEntity {

    private String street;

    private String number;

    private String floor;

    private String apartment;

    private String zipCode;

    private String details;

    @ManyToOne
    private User user;

    private boolean isPredetermined;

    private boolean active;

}

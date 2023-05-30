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
@Table(name = "phone_numbers")
public class PhoneNumber extends BaseEntity {

    private String areaCode;

    private String phoneNumber;

    @ManyToOne
    private User user;

    private boolean isPredetermined;

    private boolean active;

}

package com.lacodigoneta.elbuensabor.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "images")
public class Image extends BaseEntity {

    @Column(unique = true)
    private String location;

    private String hash;

}

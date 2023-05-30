package com.lacodigoneta.elbuensabor.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "profits")
public class Profit extends BaseEntity {

    private LocalDateTime dateTime;

    @Column(columnDefinition = "decimal(38,10)")
    private BigDecimal amount;

}

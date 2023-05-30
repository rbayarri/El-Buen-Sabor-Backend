package com.lacodigoneta.elbuensabor.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "invoices")
public class Invoice extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer number;

    @OneToOne
    private Order order;

    @OneToOne(mappedBy = "invoice")
    private CreditNote creditNote;
}

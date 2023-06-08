package com.lacodigoneta.elbuensabor.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "verify_email_tokens")
public class ForgetPasswordToken extends BaseEntity{

    @ManyToOne
    private User user;

    private LocalDate expiration;
}

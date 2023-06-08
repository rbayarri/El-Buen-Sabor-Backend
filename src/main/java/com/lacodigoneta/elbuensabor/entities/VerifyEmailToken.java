package com.lacodigoneta.elbuensabor.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "verify_email_tokens")
public class VerifyEmailToken extends BaseEntity {

    @ManyToOne
    private User user;

    private LocalDateTime expiration;
}

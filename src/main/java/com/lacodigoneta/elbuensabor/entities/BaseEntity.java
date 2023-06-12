package com.lacodigoneta.elbuensabor.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import java.util.UUID;

@MappedSuperclass
@Setter
@Getter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    private UUID id;

}

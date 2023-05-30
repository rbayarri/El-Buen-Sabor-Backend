package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.BaseEntity;

import java.util.List;
import java.util.UUID;

public interface BaseService<E extends BaseEntity> {

    List<E> findAll();

    E findById(UUID id);

    E save(E e);

    E update(UUID id, E entity);

}

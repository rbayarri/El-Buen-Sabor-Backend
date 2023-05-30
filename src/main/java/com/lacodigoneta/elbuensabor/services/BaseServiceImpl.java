package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.BaseEntity;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public abstract class BaseServiceImpl<E extends BaseEntity, R extends JpaRepository<E, UUID>> implements BaseService<E> {

    protected R repository;

    @Override
    public List<E> findAll() {
        return repository.findAll().stream().map(this::completeEntity).collect(Collectors.toList());
    }

    public Page<E> findAllPaged(Pageable pageable) {
        return repository.findAll(pageable).map(this::completeEntity);
    }

    @Override
    public E findById(UUID id) {
        return completeEntity(repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found")));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public E save(E e) {
        beforeSaveValidations(e);
        return completeEntity(repository.save(e));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public E update(UUID id, E entity) {
        E byId = findById(id);
        changeStates(entity, byId);
        return completeEntity(byId);
    }

    public abstract E changeStates(E source, E destination);

    public abstract void beforeSaveValidations(E entity);

    public E completeEntity(E entity) {
        return entity;
    }

}

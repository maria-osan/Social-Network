package com.example.socialnetwork.repository;

import com.example.socialnetwork.domain.Entity;
import com.example.socialnetwork.domain.validators.Validator;

import java.util.*;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    private final Validator<E> validator;
    protected Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<>();
    }

    @Override
    public E findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("ID must be not null!");

        return entities.get(id);
    }

    @Override
    public List<E> findAll() {
        return new ArrayList<E>(entities.values());
    }

    @Override
    public Optional<E> save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("Entity must be not null!");

        validator.validate(entity);
        if(entities.get(entity.getId()) != null) {
            return Optional.ofNullable(entity);
        }
        else entities.put(entity.getId(),entity);
        return null;
    }

    @Override
    public Optional<E> delete(ID id) {
        if(id == null)
            throw new IllegalArgumentException("Entity is null!");

        E entity = entities.get(id);
        if(entity == null)
            return null;

        return Optional.ofNullable(entities.remove(entity.getId()));
    }

    @Override
    public Optional<E> update(E entity) {

        if(entity == null)
            throw new IllegalArgumentException("Entity must be not null!");
        validator.validate(entity);

        entities.put(entity.getId(),entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }

        return Optional.ofNullable(entity);

    }
}

package com.example.repository;

import com.example.entity.AdEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdRepository extends CrudRepository<AdEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AdEntity a WHERE a.id = :id")
    Optional<AdEntity> findByIdWithLock(@Param("id") Long id);
}
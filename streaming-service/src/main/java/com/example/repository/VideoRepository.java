package com.example.repository;

import com.example.entity.VideoEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends CrudRepository<VideoEntity, Long> {
    @EntityGraph(attributePaths = {"ads"})
    List<VideoEntity> findAll();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM VideoEntity v WHERE v.id = :id")
    Optional<VideoEntity> findByIdWithLock(@Param("id") Long id);
}
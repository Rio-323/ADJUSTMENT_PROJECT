package com.example.repository;

import com.example.entity.VideoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VideoRepository extends CrudRepository<VideoEntity, Long> {
    @Override
    @EntityGraph(attributePaths = {"ads"})
    List<VideoEntity> findAll();
}

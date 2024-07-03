package com.example.repository;

import com.example.entity.AdEntity;
import org.springframework.data.repository.CrudRepository;

public interface AdRepository extends CrudRepository<AdEntity, Long> {
}

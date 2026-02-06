package com.mvc.enterprises.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mvc.enterprises.entities.KeyValuePair;

public interface KeyValuePairRepository extends JpaRepository<KeyValuePair, Integer> {
    Optional<KeyValuePair> findByCacheKey(String key); // search by cachekey
}

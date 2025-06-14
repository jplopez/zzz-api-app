package com.jplopez.zzz.repositories;

import com.jplopez.zzz.entities.WEngine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WEnginesRepository extends JpaRepository<WEngine, String> {

    // Custom method to find a WEngine by name, ignoring case
    Optional<WEngine> findByNameIgnoreCase(String name);
}
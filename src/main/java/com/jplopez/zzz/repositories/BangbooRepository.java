package com.jplopez.zzz.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jplopez.zzz.entities.Bangboo;
import com.jplopez.zzz.entities.enums.Rarity;

/**
 * Repository to read data of Bangboos.
 * Repositories expose the operations available to do against a specific table.
 * 
 * @since 1.0
 */
@Repository
public interface BangbooRepository extends JpaRepository<Bangboo, String> {

  List<Bangboo> findByNameIgnoreCase(String name);

  List<Bangboo> findByNameContaining(String input);

  List<Bangboo> findByRarity(Rarity rarity);

  List<Bangboo> findByRarityIn(Collection<Rarity> rarities);

  List<Bangboo> findByFactionContaining(String input);

  List<Bangboo> findByVersion(Float version);

  List<Bangboo> findByVersionBetween(Float from, Float to);

  List<Bangboo> findByVersionIn(Collection<Float> versions);

  List<Bangboo> findByNameOrRarityOrFactionOrVersion(
      String name, Rarity rarity, String faction, Float version);
}
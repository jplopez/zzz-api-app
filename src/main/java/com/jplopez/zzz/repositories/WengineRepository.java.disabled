package com.jplopez.zzz.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jplopez.zzz.entities.WEngine;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialties;

/**
 * Repository to read data of Wengines.
 * Repositories expose the operations available to do against a specific table.
 * 
 * @since 1.0
 */
@Repository
public interface WEngineRepository extends JpaRepository<WEngine, String> {

  List<WEngine> findByNameIgnoreCase(String name);

  List<WEngine> findByNameContaining(String input);

  List<WEngine> findByRarity(Rarity rarity);

  List<WEngine> findByRarityIn(Collection<Rarity> rarities);

  List<WEngine> findBySpecialty(Specialties specialty);

  List<WEngine> findBySpecialtyIn(Collection<Specialties> specialties);

  List<WEngine> findByBaseAttack(Integer baseAttack);

  List<WEngine> findByBaseAttackBetween(Integer from, Integer to);

  List<WEngine> findByDescriptionContaining(String input);

  List<WEngine> findByVersion(Float version);

  List<WEngine> findByVersionBetween(Float from, Float to);

  List<WEngine> findByVersionIn(Collection<Float> versions);

  List<WEngine> findByNameOrRarityOrSpecialtyOrBaseAttack(
      String name, Rarity rarity, Specialties specialty, Integer baseAttack);
}
package com.jplopez.zzz.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jplopez.zzz.entities.Wengine;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialities;

/**
 * Repository to read data of Wengines.
 * Repositories expose the operations available to do against a specific table.
 * 
 * @since 1.0
 */
@Repository
public interface WengineRepository extends JpaRepository<Wengine, String> {

  List<Wengine> findByNameIgnoreCase(String name);

  List<Wengine> findByNameContaining(String input);

  List<Wengine> findByRarity(Rarity rarity);

  List<Wengine> findByRarityIn(Collection<Rarity> rarities);

  List<Wengine> findBySpecialty(Specialities specialty);

  List<Wengine> findBySpecialtyIn(Collection<Specialities> specialties);

  List<Wengine> findByBaseAttack(Integer baseAttack);

  List<Wengine> findByBaseAttackBetween(Integer from, Integer to);

  List<Wengine> findByDescriptionContaining(String input);

  List<Wengine> findByNameOrRarityOrSpecialtyOrBaseAttack(
      String name, Rarity rarity, Specialities specialty, Integer baseAttack);
}
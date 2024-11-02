package com.jplopez.zzz.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jplopez.zzz.entities.Agent;
import com.jplopez.zzz.entities.enums.Attributes;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialities;
import com.jplopez.zzz.entities.enums.Type;

@Repository
public interface AgentsRepository extends JpaRepository<Agent, String> {

  List<Agent> findByNameIgnoreCase(String name);

  List<Agent> findByFullNameContaining(String input);

  List<Agent> findByRarity(Rarity rarity);

  List<Agent> findByAttribute(Attributes attribute);

  List<Agent> findByAttributeIn(Collection<Attributes> attributes);

  List<Agent> findBySpeciality(Specialities speciality);

  List<Agent> findBySpecialityIn(Collection<Specialities> specialities);

  List<Agent> findByType(Type type);

  List<Agent> findByTypeIn(Collection<Type> types);

  List<Agent> findByFactionContaining(String input);

  List<Agent> findByVersion(Double version);

  List<Agent> findByVersionIn(Collection<Double> versions);

  List<Agent> findByVersionBetween(Double from, Double to);

  List<Agent> findByNameOrRarityOrAttributeOrSpecialityOrTypeOrFactionOrVersion(
      String name, Rarity rarity, Attributes attribute, Specialities speciality, Type type, String faction, Double version);
}

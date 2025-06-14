package com.jplopez.zzz.repositories;

import java.util.Collection;
import java.util.List;

import com.jplopez.zzz.entities.Agent;
import com.jplopez.zzz.entities.enums.Attributes;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialities;
import com.jplopez.zzz.entities.enums.Type;

/**
 * Repository to read data of Agents.
 * Repositories expose the operations available to do against a specific table.
 * 
 * @since 1.0
 */
public interface AgentsRepository extends ZZZRepository<Agent> {

  List<Agent> findByFullNameContaining(String input);

  List<Agent> findByRarity(Rarity rarity);

  List<Agent> findByAttribute(Attributes attribute);

  List<Agent> findByAttributeIn(Collection<Attributes> attributes);

  List<Agent> findBySpeciality(Specialities speciality);

  List<Agent> findBySpecialityIn(Collection<Specialities> specialities);

  List<Agent> findByType(Type type);

  List<Agent> findByTypeIn(Collection<Type> types);

  List<Agent> findByFactionContaining(String input);

  List<Agent> findByNameOrRarityOrAttributeOrSpecialityOrTypeOrFactionOrVersion(
      String name, Rarity rarity, Attributes attribute, Specialities speciality, Type type, String faction, Double version);
}

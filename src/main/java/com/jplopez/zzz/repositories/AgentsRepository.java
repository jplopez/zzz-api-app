package com.jplopez.zzz.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jplopez.zzz.entities.Agent;

@Repository
public interface AgentsRepository extends JpaRepository<Agent, String> {

  List<Agent> findByName(String name);

  List<Agent> findByRarity(String rarity);

  List<Agent> findByElement(String element);

  List<Agent> findByStyle(String style);

  List<Agent> findByAttackStyle(String attackStyle);

  List<Agent> findByFaction(String faction);

  List<Agent> findByVersion(Double version);

  List<Agent> findByNameOrRarityOrElementOrStyleOrAttackStyleOrFactionOrVersion(
      String name, String rarity, String element, String style, String styleAttack, String faction, Double version);
}

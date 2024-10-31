package com.jplopez.zzz.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jplopez.zzz.entities.Agent;

@Repository
public interface AgentsRepository extends JpaRepository<Agent, String> {

  List<Agent> findByNameIgnoreCase(String name);

  List<Agent> findByRarityIgnoreCase(String rarity);

  List<Agent> findByElementIgnoreCase(String element);

  List<Agent> findByStyleIgnoreCase(String style);

  List<Agent> findByAttackStyleIgnoreCase(String attackStyle);

  List<Agent> findByFactionIgnoreCase(String faction);

  List<Agent> findByVersion(Double version);

  List<Agent> findByNameOrRarityOrElementOrStyleOrAttackStyleOrFactionOrVersionAllIgnoreCase(
      String name, String rarity, String element, String style, String styleAttack, String faction, Double version);
}

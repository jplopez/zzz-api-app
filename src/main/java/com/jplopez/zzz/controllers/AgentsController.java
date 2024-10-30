package com.jplopez.zzz.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jplopez.zzz.common.exceptions.NotFoundException;
import com.jplopez.zzz.entities.Agent;
import com.jplopez.zzz.repositories.AgentsRepository;

@RestController
@RequestMapping(path = { "/agents", "/a" })
public class AgentsController {

  private final AgentsRepository repository;

  AgentsController(AgentsRepository repository) {
      this.repository = repository;
  }

  @GetMapping
  public Iterable<Agent> findAll() {
    return repository.findAll();
  }

  @GetMapping("/{id}")
  public Agent findOne(@PathVariable String id) {
    return repository.findById(id)
        .orElseThrow(NotFoundException::new);
  }

  @GetMapping("/q?name={value}")
  public List<Agent> findByName(@PathVariable String value) {
    return repository.findByName(value);
  }

  @GetMapping("/q?rarity={value}")
  public List<Agent> findByRarity(@PathVariable String value) {
    return repository.findByName(value);
  }

  @GetMapping("/q?element={value}")
  public List<Agent> findByElement(@PathVariable String value) {
    return repository.findByElement(value);
  }

  @GetMapping("/q?style={value}")
  public List<Agent> findByStyle(@PathVariable String value) {
    return repository.findByStyle(value);
  }

  @GetMapping("/q?attackStyle={value}")
  public List<Agent> findByAttackStyle(@PathVariable String value) {
    return repository.findByAttackStyle(value);
  }

  @GetMapping("/q?faction={value}")
  public List<Agent> findByFaction(@PathVariable String value) {
    return repository.findByFaction(value);
  }

  @GetMapping("/q?version={value}")
  public List<Agent> findByVersion(@PathVariable String value) {
    return repository.findByVersion(Double.valueOf(value));
  }

  @GetMapping("/q?name={name}&rarity={rarity}&element={element}&style={style}&attackStyle={attackStyle}&faction={faction}&version={version}")
  public List<Agent> queryAgents(@PathVariable String name, @PathVariable String rarity, @PathVariable String element,
      @PathVariable String style, @PathVariable String attackStyle, @PathVariable String faction,
      @PathVariable String version) {
    return repository.findByNameOrRarityOrElementOrStyleOrAttackStyleOrFactionOrVersion(name, rarity, element,
        attackStyle, style, faction, Double.valueOf(version));
  }

}

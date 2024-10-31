package com.jplopez.zzz.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  public List<Agent> findAll() {
    return new ArrayList<>(repository.findAll());
  }

  @GetMapping("/{id}")
  public Agent findOne(@PathVariable String id) {
    return repository.findById(id)
        .orElseThrow(NotFoundException::new);
  }

  @GetMapping("/name/{value}")
  public List<Agent> findByName(@PathVariable String value) {
    return repository.findByNameIgnoreCase(value);
  }

  @GetMapping("/rarity/{value}")
  public List<Agent> findByRarity(@PathVariable String value) {
    return repository.findByRarityIgnoreCase(value);
  }

  @GetMapping("/element/{value}")
  public List<Agent> findByElement(@PathVariable String value) {
    return repository.findByElementIgnoreCase(value);
  }

  @GetMapping("/style/{value}")
  public List<Agent> findByStyle(@PathVariable String value) {
    return repository.findByStyleIgnoreCase(value);
  }

  @GetMapping("/attackStyle/{value}")
  public List<Agent> findByAttackStyle(@PathVariable String value) {
    return repository.findByAttackStyleIgnoreCase(value);
  }

  @GetMapping("/faction/{value}")
  public List<Agent> findByFaction(@PathVariable String value) {
    return repository.findByFactionIgnoreCase(value);
  }

  @GetMapping("/version/{value}")
  public List<Agent> findByVersion(@PathVariable String value) {
    return repository.findByVersion(Double.valueOf(value));
  }

  @GetMapping("/q")
  public List<Agent> queryAgents(@RequestParam Map<String,String> queryParams) {
    if(queryParams.isEmpty()) return findAll();
    return repository.findByNameOrRarityOrElementOrStyleOrAttackStyleOrFactionOrVersionAllIgnoreCase(
        queryParams.get("name"), queryParams.get("rarity"), queryParams.get("element"), 
        queryParams.get("style"), queryParams.get("attackStyle"), 
        queryParams.get("faction"), NumberUtils.toDouble(queryParams.get("version")));
  }

}

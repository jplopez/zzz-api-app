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
import com.jplopez.zzz.entities.enums.Attributes;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialities;
import com.jplopez.zzz.entities.enums.Type;
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

  @GetMapping("/name?{value}")
  public List<Agent> findByNameLike(@PathVariable String value) {
    return repository.findByFullNameContaining(value);
  }

  @GetMapping("/rarity/{value}")
  public List<Agent> findByRarity(@PathVariable("value") Rarity rarity) {
    return repository.findByRarity(rarity);
  }

  @GetMapping("/attribute/{value}")
  public List<Agent> findByAttribute(@PathVariable("value") Attributes attr) {
    return repository.findByAttribute(attr);
  }

  @GetMapping("/speciality/{value}")
  public List<Agent> findBySpeciality(@PathVariable("value") Specialities spec) {
    return repository.findBySpeciality(spec);
  }

  @GetMapping("/type/{value}")
  public List<Agent> findByType(@PathVariable("value") Type typ) {
    return repository.findByType(typ);
  }

  @GetMapping("/faction?{value}")
  public List<Agent> findByFaction(@PathVariable("value") String value) {
    return repository.findByFactionContaining(value);
  }

  @GetMapping("/version/{value}")
  public List<Agent> findByVersion(@PathVariable String value) {
    return repository.findByVersion(Double.valueOf(value));
  }

  @GetMapping("/version/{from}/{to}")
  public List<Agent> findByVersion(@PathVariable String from, @PathVariable String to) {
    return repository.findByVersionBetween(Double.valueOf(from), Double.valueOf(to));
  }

  @GetMapping("/q")
  public List<Agent> queryAgents(@RequestParam Map<String,String> queryParams) {
    if(queryParams.isEmpty()) return findAll();
    return repository.findByNameOrRarityOrAttributeOrSpecialityOrTypeOrFactionOrVersion(
            queryParams.get("name"), 
            Rarity.valueOf(queryParams.get("rarity").toUpperCase()), 
            Attributes.valueOf(queryParams.get("attribute").toUpperCase()), 
            Specialities.valueOf(queryParams.get("speciality").toUpperCase()), 
            Type.valueOf(queryParams.get("type").toUpperCase()), 
            queryParams.get("faction"), 
            NumberUtils.toDouble(queryParams.get("version")));
  }

}

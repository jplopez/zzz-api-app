package com.jplopez.zzz.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jplopez.zzz.entities.WEngine;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialties;
import com.jplopez.zzz.repositories.WEngineRepository;

/** Read-only W-Engine catalog. */
@RestController
@RequestMapping(path = { "/wengines", "/w" })
public class WEngineController extends ZZZController<WEngine, WEngineRepository> {

  WEngineController(WEngineRepository repository) {
    super("wengines", repository);
  }

  @Override
  protected WEngine withLinks(WEngine wengine) {
    return wengine.add(selfRelLink(wengine.getId()));
  }

  @GetMapping("/name/{value}")
  public List<WEngine> findByName(@PathVariable String value) {
    return withLinks(repository.findByNameIgnoreCase(value));
  }

  @GetMapping("/name_like/{value}")
  public List<WEngine> findByNameLike(@PathVariable String value) {
    return withLinks(repository.findByNameContaining(value));
  }

  @GetMapping("/rarity/{value}")
  public List<WEngine> findByRarity(@PathVariable Rarity value) {
    return withLinks(repository.findByRarity(value));
  }

  @GetMapping("/specialty/{value}")
  public List<WEngine> findBySpecialty(@PathVariable Specialties value) {
    return withLinks(repository.findBySpecialty(value));
  }

  @GetMapping("/base_attack/{value}")
  public List<WEngine> findByBaseAttack(@PathVariable int value) {
    return withLinks(repository.findByBaseAttack(value));
  }

  @GetMapping("/base_attack_between/{from}/{to}")
  public List<WEngine> findByBaseAttackBetween(@PathVariable int from, @PathVariable int to) {
    validateRange(from, to);
    return withLinks(repository.findByBaseAttackBetween(from, to));
  }

  @GetMapping("/description_like/{value}")
  public List<WEngine> findByDescriptionLike(@PathVariable String value) {
    return withLinks(repository.findByDescriptionContaining(value));
  }

  @GetMapping("/version/{value}")
  public List<WEngine> findByVersion(@PathVariable float value) {
    validateVersion(value);
    return withLinks(repository.findByVersion(value));
  }

  @GetMapping("/version/{from}/{to}")
  public List<WEngine> findByVersion(@PathVariable float from, @PathVariable float to) {
    validateVersion(from);
    validateVersion(to);
    validateRange(from, to);
    return withLinks(repository.findByVersionBetween(from, to));
  }
}

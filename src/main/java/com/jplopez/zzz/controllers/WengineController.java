package com.jplopez.zzz.controllers;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.jplopez.zzz.common.exceptions.ZZZEntityNotFoundException;
import com.jplopez.zzz.entities.WEngine;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialties;

import com.jplopez.zzz.repositories.WEngineRepository;

/**
 * Rest Controller to retrieve data from Wengines
 * 
 * @since 1.0
 */
@RestController
@RequestMapping(path = { "/wengines", "/w" })
public class WEngineController extends ZZZController<WEngine, WEngineRepository> {


  WEngineController(WEngineRepository repository) {
      super("wengines", repository);
  }

  @Override
  @GetMapping(produces = { "application/hal+json" })
  public CollectionModel<WEngine> findAll() {

    List<WEngine> wengines = repository.findAll();
    for(WEngine w : wengines) {
      String wengineId = w.getId();
      Link selfLink = linkTo(WEngineController.class).slash(wengineId).withSelfRel();
      w.add(selfLink);
    }
    Link link = linkTo(WEngineController.class).withSelfRel();
    return CollectionModel.of(wengines, link);
  }

  @Override
  @GetMapping("/{id}")
  public WEngine findOne(@PathVariable String id) {
    return repository.findById(id)
        .map(wengine -> wengine.add(linkTo(methodOn(WEngineController.class).findOne(wengine.getId())).withSelfRel()))
        .orElseThrow(ZZZEntityNotFoundException::new);
  }

  @GetMapping("/name/{value}")
  public List<WEngine> findByName(@PathVariable String value) {
    return repository.findByNameIgnoreCase(value).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WEngineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }

  @GetMapping("/name_like/{value}")
  public List<WEngine> findByNameLike(@PathVariable String value) {
    return repository.findByNameContaining(value).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WEngineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }

  @GetMapping("/rarity/{value}")
  public List<WEngine> findByRarity(@PathVariable("value") Rarity rarity) {
    return repository.findByRarity(rarity).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WEngineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }

  @GetMapping("/specialty/{value}")
  public List<WEngine> findBySpecialty(@PathVariable("value") Specialties specialty) {
    return repository.findBySpecialty(specialty).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WEngineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }

  @GetMapping("/base_attack/{value}")
  public List<WEngine> findByBaseAttack(@PathVariable Integer value) {
    return repository.findByBaseAttack(value).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WEngineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }

  @GetMapping("/base_attack_between/{from}/{to}")
  public List<WEngine> findByBaseAttackBetween(@PathVariable Integer from, @PathVariable Integer to) {
    return repository.findByBaseAttackBetween(from, to).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WEngineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }

  @GetMapping("/description_like/{value}")
  public List<WEngine> findByDescriptionLike(@PathVariable String value) {
    return repository.findByDescriptionContaining(value).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WEngineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }

  @Override
  @GetMapping("/version/{value}")
  public List<WEngine> findByVersion(@PathVariable String value) {
    return repository.findByVersion(Float.valueOf(value)).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WEngineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }

  @Override
  @GetMapping("/version/{from}/{to}")
  public List<WEngine> findByVersion(@PathVariable String from, @PathVariable String to) {
    return repository.findByVersionBetween(Float.valueOf(from), Float.valueOf(to)).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WEngineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }
}
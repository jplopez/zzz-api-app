package com.jplopez.zzz.controllers;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.jplopez.zzz.common.exceptions.NotFoundException;
import com.jplopez.zzz.entities.Wengine;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialities;
import com.jplopez.zzz.repositories.WengineRepository;

/**
 * Rest Controller to retrieve data from Wengines
 * 
 * @since 1.0
 */
@RestController
@RequestMapping(path = { "/wengines", "/w" })
public class WengineController {

  private final WengineRepository repository;

  WengineController(WengineRepository repository) {
      this.repository = repository;
  }

  @GetMapping(produces = { "application/hal+json" })
  public CollectionModel<Wengine> findAll() {
    List<Wengine> wengines = repository.findAll().stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WengineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
    Link link = linkTo(WengineController.class).withSelfRel();
    return CollectionModel.of(wengines, link);
  }

  @GetMapping("/{id}")
  public Wengine findOne(@PathVariable String id) {
    return repository.findById(id)
        .map(wengine -> wengine.add(linkTo(methodOn(WengineController.class).findOne(wengine.getId())).withSelfRel()))
        .orElseThrow(() -> new NotFoundException());
  }

  @GetMapping("/name/{value}")
  public List<Wengine> findByName(@PathVariable String value) {
    return repository.findByNameIgnoreCase(value).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WengineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }

  @GetMapping("/name_like/{value}")
  public List<Wengine> findByNameLike(@PathVariable String value) {
    return repository.findByNameContaining(value).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WengineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }

  @GetMapping("/rarity/{value}")
  public List<Wengine> findByRarity(@PathVariable("value") Rarity rarity) {
    return repository.findByRarity(rarity).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WengineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }

  @GetMapping("/specialty/{value}")
  public List<Wengine> findBySpecialty(@PathVariable("value") Specialities specialty) {
    return repository.findBySpecialty(specialty).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WengineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }

  @GetMapping("/base_attack/{value}")
  public List<Wengine> findByBaseAttack(@PathVariable Integer value) {
    return repository.findByBaseAttack(value).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WengineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }

  @GetMapping("/base_attack_between/{from}/{to}")
  public List<Wengine> findByBaseAttackBetween(@PathVariable Integer from, @PathVariable Integer to) {
    return repository.findByBaseAttackBetween(from, to).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WengineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }

  @GetMapping("/description_like/{value}")
  public List<Wengine> findByDescriptionLike(@PathVariable String value) {
    return repository.findByDescriptionContaining(value).stream()
        .map(wengine -> wengine.add(linkTo(methodOn(WengineController.class).findOne(wengine.getId())).withSelfRel()))
        .toList();
  }
}
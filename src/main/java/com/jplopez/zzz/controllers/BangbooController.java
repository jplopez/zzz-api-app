package com.jplopez.zzz.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jplopez.zzz.entities.Bangboo;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.repositories.BangbooRepository;

/**
 * Rest Controller to retrieve data from Bangboos
 * 
 * @since 1.0
 */
@RestController
@RequestMapping(path = { "/bangboos", "/b" })
public class BangbooController extends ZZZController<Bangboo, BangbooRepository> {

  BangbooController(BangbooRepository repository) {
    super("bangboos", repository);
  }

  @Override
  @GetMapping(produces = { "application/hal+json" })
  public CollectionModel<Bangboo> findAll() {
    List<Bangboo> bangboos = repository.findAll();
    
    // Add self links to each bangboo
    bangboos.forEach(bangboo -> bangboo.add(selfRelLink(bangboo.getId())));
    
    Link link = linkTo(BangbooController.class).withSelfRel();
    return CollectionModel.of(bangboos, link);
  }

  @Override
  @GetMapping("/{id}")
  public Bangboo findOne(@PathVariable String id) {
    Bangboo bangboo = super.findOne(id);
    bangboo.add(selfRelLink(bangboo.getId()));
    return bangboo;
  }

  @GetMapping("/name/{value}")
  public List<Bangboo> findByName(@PathVariable String value) {
    List<Bangboo> bangboos = repository.findByNameIgnoreCase(value);
    bangboos.forEach(bangboo -> bangboo.add(selfRelLink(bangboo.getId())));
    return bangboos;
  }

  @GetMapping("/name_like/{value}")
  public List<Bangboo> findByNameLike(@PathVariable String value) {
    List<Bangboo> bangboos = repository.findByNameContaining(value);
    bangboos.forEach(bangboo -> bangboo.add(selfRelLink(bangboo.getId())));
    return bangboos;
  }

  @GetMapping("/rarity/{value}")
  public List<Bangboo> findByRarity(@PathVariable("value") Rarity rarity) {
    List<Bangboo> bangboos = repository.findByRarity(rarity);
    bangboos.forEach(bangboo -> bangboo.add(selfRelLink(bangboo.getId())));
    return bangboos;
  }

  @GetMapping("/faction/{value}")
  public List<Bangboo> findByFaction(@PathVariable String value) {
    List<Bangboo> bangboos = repository.findByFactionIgnoreCase(value);
    bangboos.forEach(bangboo -> bangboo.add(selfRelLink(bangboo.getId())));
    return bangboos;
  }

  @GetMapping("/faction_like/{value}")
  public List<Bangboo> findByFactionLike(@PathVariable String value) {
    List<Bangboo> bangboos = repository.findByFactionContaining(value);
    bangboos.forEach(bangboo -> bangboo.add(selfRelLink(bangboo.getId())));
    return bangboos;
  }

  @GetMapping("/bangboo_id/{value}")
  public List<Bangboo> findByBangbooId(@PathVariable String value) {
    List<Bangboo> bangboos = repository.findByBangbooId(value);
    bangboos.forEach(bangboo -> bangboo.add(selfRelLink(bangboo.getId())));
    return bangboos;
  }

  @Override
  public List<Bangboo> findByVersion(@PathVariable String version) {
    // Bangboo entity does not have version field, return empty list
    return List.of();
  }

  @Override
  public List<Bangboo> findByVersion(@PathVariable String from, @PathVariable String to) {
    // Bangboo entity does not have version field, return empty list
    return List.of();
  }

}
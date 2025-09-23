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

    for(Bangboo b : bangboos) {
      String bangbooId = b.getId();
      Link selfLink = linkTo(BangbooController.class).slash(bangbooId).withSelfRel();
      b.add(selfLink);
    }
    Link link = linkTo(BangbooController.class).withSelfRel();
    return CollectionModel.of(bangboos, link);
  }

  @GetMapping("/name/{value}")
  public List<Bangboo> findByName(@PathVariable String value) {
    List<Bangboo> results = repository.findByNameIgnoreCase(value);
    for(Bangboo bangboo : results) {
      bangboo.add(linkTo(methodOn(BangbooController.class).findOne(bangboo.getId())).withSelfRel());
    }
    return results;
  }

  @GetMapping("/name_like/{value}")
  public List<Bangboo> findByNameLike(@PathVariable String value) {
    List<Bangboo> results = repository.findByNameContaining(value);
    for(Bangboo bangboo : results) {
      bangboo.add(linkTo(methodOn(BangbooController.class).findOne(bangboo.getId())).withSelfRel());
    }
    return results;
  }

  @GetMapping("/rarity/{value}")
  public List<Bangboo> findByRarity(@PathVariable("value") Rarity rarity) {
    List<Bangboo> results = repository.findByRarity(rarity);
    for(Bangboo bangboo : results) {
      bangboo.add(linkTo(methodOn(BangbooController.class).findOne(bangboo.getId())).withSelfRel());
    }
    return results;
  }

  @GetMapping("/faction_like/{value}")
  public List<Bangboo> findByFaction(@PathVariable("value") String value) {
    List<Bangboo> results = repository.findByFactionContaining(value);
    for(Bangboo bangboo : results) {
      bangboo.add(linkTo(methodOn(BangbooController.class).findOne(bangboo.getId())).withSelfRel());
    }
    return results;
  }

  @Override
  @GetMapping("/version/{value}")
  public List<Bangboo> findByVersion(@PathVariable String value) {
    List<Bangboo> results = repository.findByVersion(Float.valueOf(value));
    for(Bangboo bangboo : results) {
      bangboo.add(linkTo(methodOn(BangbooController.class).findOne(bangboo.getId())).withSelfRel());
    }
    return results;
  }

  @Override
  @GetMapping("/version/{from}/{to}")
  public List<Bangboo> findByVersion(@PathVariable String from, @PathVariable String to) {
    List<Bangboo> results = repository.findByVersionBetween(Float.valueOf(from), Float.valueOf(to));
    for(Bangboo bangboo : results) {
      bangboo.add(linkTo(methodOn(BangbooController.class).findOne(bangboo.getId())).withSelfRel());
    }
    return results;
  }
}
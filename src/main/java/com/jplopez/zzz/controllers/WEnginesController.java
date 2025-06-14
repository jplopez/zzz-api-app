package com.jplopez.zzz.controllers;

import com.jplopez.zzz.entities.WEngine;
import com.jplopez.zzz.common.exceptions.ZZZEntityNotFoundException;
import com.jplopez.zzz.repositories.WEnginesRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping(path = { "/wengines", "/w" })
public class WEnginesController {

  private final WEnginesRepository repository;

  WEnginesController(WEnginesRepository repository) {
    this.repository = repository;
  }

  // Aggregate root
  // tag::get-aggregate-root[]
  @GetMapping(produces = { "application/hal+json" })
  public CollectionModel<WEngine> findAll() {

    List<WEngine> wEngines = repository.findAll();
    
    for(WEngine wEngine : wEngines) {
      wEngine.add(linkTo(methodOn(WEnginesController.class)
          .findOne(wEngine.getName())).withSelfRel());
    }
    return CollectionModel.of(wEngines, 
      linkTo(methodOn(WEnginesController.class).findAll()).withSelfRel());
  }
  // end::get-aggregate-root[]

  // Single item
  // tag::get-item[]
  @GetMapping("/{name}")
  public EntityModel<WEngine> findOne(@PathVariable String name) {
    WEngine wEngine = repository.findByNameIgnoreCase(name)
        .orElseThrow(() -> new ZZZEntityNotFoundException(
            "WEngine not found with name: " + name));

    return EntityModel.of(wEngine, //
        linkTo(methodOn(WEnginesController.class).findOne(name)).withSelfRel(),
        linkTo(methodOn(WEnginesController.class).findAll()).withRel("wengines"));
  }
  // end::get-item[]

  // Find by Specialty
  @GetMapping("/specialty/{value}")
  public CollectionModel<EntityModel<WEngine>> findBySpecialty(@PathVariable String value) {
    // Assuming Specialty enum has a valueOf method or similar lookup
    // For simplicity, this example assumes the enum name matches the string value
    // You might need more robust handling for case sensitivity or invalid values
    List<EntityModel<WEngine>> wEngines = repository.findAll().stream()
        .filter(wEngine -> wEngine.getSpecialty().name().equalsIgnoreCase(value))
        .map(wEngine -> EntityModel.of(wEngine,
            linkTo(methodOn(WEnginesController.class).findOne(wEngine.getName()))
                .withSelfRel(),
            linkTo(methodOn(WEnginesController.class).findAll())
                .withRel("wengines")))
        .toList();

    return CollectionModel.of(wEngines,
        linkTo(methodOn(WEnginesController.class).findBySpecialty(value)).withSelfRel());
  }

  // Find by SubStatName
  @GetMapping("/substatname/{value}")
  public CollectionModel<EntityModel<WEngine>> findBySubStatName(@PathVariable String value) {
    List<EntityModel<WEngine>> wEngines = repository.findAll().stream()
        .filter(wEngine -> wEngine.getSubStatName().equalsIgnoreCase(value))
        .map(wEngine -> EntityModel.of(wEngine,
            linkTo(methodOn(WEnginesController.class).findOne(wEngine.getName()))
                .withSelfRel(),
            linkTo(methodOn(WEnginesController.class).findAll())
                .withRel("wengines")))
        .toList();

    return CollectionModel.of(wEngines,
        linkTo(methodOn(WEnginesController.class).findBySubStatName(value)).withSelfRel());
  }

  // Find by SkillName
  @GetMapping("/skillname/{value}")
  public CollectionModel<EntityModel<WEngine>> findBySkillName(@PathVariable String value) {
    List<EntityModel<WEngine>> wEngines = repository.findAll().stream()
        .filter(wEngine -> wEngine.getSkillName().equalsIgnoreCase(value))
        .map(wEngine -> EntityModel.of(wEngine,
            linkTo(methodOn(WEnginesController.class).findOne(wEngine.getName()))
                .withSelfRel(),
            linkTo(methodOn(WEnginesController.class).findAll())
                .withRel("wengines")))
        .toList();

    return CollectionModel.of(wEngines,
        linkTo(methodOn(WEnginesController.class).findBySkillName(value)).withSelfRel());
  }
}
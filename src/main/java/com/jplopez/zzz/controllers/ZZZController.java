package com.jplopez.zzz.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jplopez.zzz.common.exceptions.ZZZEntityNotFoundException;
import com.jplopez.zzz.entities.ZZZEntityModel;
import com.jplopez.zzz.repositories.ZZZRepository;

@RestController
public abstract class ZZZController<T extends ZZZEntityModel<T>, R extends ZZZRepository<T>> {

  protected final String path;

  protected final R repository;

  ZZZController(String path, R repository) {
    this.path = path;
    this.repository = repository;
  }

  public String getPath() {
    return path;
  }

  public R getRepository() {
    return repository;
  }

  @GetMapping(produces = { "application/hal+json" })
  public CollectionModel<T> findAll() {
    List<T> entities = repository.findAll();

    for (T entity : entities) {
      entity.add(linkTo(methodOn(WEnginesController.class)
          .findOne(entity.getName())).withSelfRel());
    }
    return CollectionModel.of(entities,
        linkTo(methodOn(WEnginesController.class).findAll()).withSelfRel());
  }

  @GetMapping("/{id}")
  public T findOne(@PathVariable String id) {
    return repository.findById(id)
        .orElseThrow(ZZZEntityNotFoundException::new);
  }

  @GetMapping("/name/{value}")
  public List<T> findByName(@PathVariable String value) {
    return repository.findByNameIgnoreCase(value);
  }

  @GetMapping("/version/{value}")
  public abstract List<T> findByVersion(@PathVariable String value);

  @GetMapping("/version/{from}/{to}")
  public abstract List<T> findByVersion(@PathVariable String from, @PathVariable String to);

  protected Link selfRelLink(String entityId) {
    return linkTo(this.getClass()).slash(entityId).withSelfRel();
  }

  protected Link RelLink(String id) {
    return Link.of("/" + path + "/{id}", id);
  }

  protected abstract String getID();
}
package com.jplopez.zzz.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.jplopez.zzz.common.exceptions.ZZZEntityNotFoundException;

public abstract class ZZZController<T extends RepresentationModel<T>, R extends JpaRepository<T, String>> {

  protected final String path;

  protected final R repository;

  protected ZZZController(String path, R repository) {
    this.path = path;
    this.repository = repository;
  }

  public String getPath() {
    return path;
  }

  public R getRepository() {
    return repository;
  }

  @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
  public CollectionModel<T> findAll() {
    return CollectionModel.of(withLinks(repository.findAll()), linkTo(getClass()).withSelfRel());
  }

  @GetMapping("/{id}")
  public T findOne(@PathVariable String id) {
    return withLinks(repository.findById(id).orElseThrow(ZZZEntityNotFoundException::new));
  }

  protected T withLinks(T entity) {
    return entity;
  }

  protected List<T> withLinks(List<T> entities) {
    return entities.stream().map(this::withLinks).toList();
  }

  protected Link selfRelLink(String entityId) {
    return linkTo(getClass()).slash(entityId).withSelfRel();
  }

  protected static void validateRange(double from, double to) {
    if (!Double.isFinite(from) || !Double.isFinite(to) || from > to) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Range bounds must be finite and in ascending order");
    }
  }

  protected static void validateVersion(float version) {
    if (!Float.isFinite(version) || version <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Version must be a positive number");
    }
  }
}

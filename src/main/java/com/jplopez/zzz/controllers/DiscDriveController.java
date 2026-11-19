package com.jplopez.zzz.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.jplopez.zzz.entities.DiscDrive;
import com.jplopez.zzz.entities.enums.StatTypes;
import com.jplopez.zzz.repositories.DiscDriveRepository;

/** Read-only catalog of individual Drive Discs. */
@RestController
@RequestMapping(path = { "/discdrives", "/dd", "/discs" })
public class DiscDriveController extends ZZZController<DiscDrive, DiscDriveRepository> {

  DiscDriveController(DiscDriveRepository repository) {
    super("discdrives", repository);
  }

  @Override
  protected DiscDrive withLinks(DiscDrive disc) {
    return addDiscLinks(disc);
  }

  static DiscDrive addDiscLinks(DiscDrive disc) {
    disc.add(linkTo(DiscDriveController.class).slash(disc.getId()).withSelfRel());
    if (disc.getDiscDriveSetId() != null) {
      disc.add(linkTo(DiscDriveSetController.class).slash(disc.getDiscDriveSetId()).withRel("set"));
    }
    return disc;
  }

  @GetMapping("/position/{value}")
  public List<DiscDrive> findByPosition(@PathVariable int value) {
    if (value < 1 || value > 6) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Disc position must be between 1 and 6");
    }
    return withLinks(repository.findByPosition(value));
  }

  @GetMapping("/type/{value}")
  public List<DiscDrive> findByType(@PathVariable StatTypes value) {
    return withLinks(repository.findByType(value));
  }

  @GetMapping("/base_value/{value}")
  public List<DiscDrive> findByBaseValue(@PathVariable float value) {
    if (!Float.isFinite(value)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Base value must be a finite number");
    }
    return withLinks(repository.findByBaseValue(value));
  }

  @GetMapping("/base_value_between/{from}/{to}")
  public List<DiscDrive> findByBaseValueBetween(@PathVariable float from, @PathVariable float to) {
    validateRange(from, to);
    return withLinks(repository.findByBaseValueBetween(from, to));
  }

  @GetMapping("/set/{id}")
  public List<DiscDrive> findBySet(@PathVariable String id) {
    return withLinks(repository.findByDiscDriveSetIdOrderByPositionAscIdAsc(id));
  }
}

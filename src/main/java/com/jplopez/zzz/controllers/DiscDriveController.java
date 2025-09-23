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

import com.jplopez.zzz.entities.DiscDrive;
import com.jplopez.zzz.entities.enums.StatTypes;
import com.jplopez.zzz.repositories.DiscDriveRepository;

/**
 * Rest Controller to retrieve data from Disc Drives
 * 
 * @since 1.0
 */
@RestController
@RequestMapping(path = { "/discdrives", "/dd" })
public class DiscDriveController extends ZZZController<DiscDrive, DiscDriveRepository> {

  DiscDriveController(DiscDriveRepository repository) {
    super("discdrives", repository);
  }

  @Override
  @GetMapping(produces = { "application/hal+json" })
  public CollectionModel<DiscDrive> findAll() {
    List<DiscDrive> discDrives = repository.findAll();
    
    // Add self links to each disc drive
    discDrives.forEach(dd -> dd.add(selfRelLink(dd.getId())));
    
    Link link = linkTo(DiscDriveController.class).withSelfRel();
    return CollectionModel.of(discDrives, link);
  }

  @Override
  @GetMapping("/{id}")
  public DiscDrive findOne(@PathVariable String id) {
    DiscDrive discDrive = super.findOne(id);
    discDrive.add(selfRelLink(discDrive.getId()));
    return discDrive;
  }

  @GetMapping("/position/{value}")
  public List<DiscDrive> findByPosition(@PathVariable int value) {
    List<DiscDrive> discDrives = repository.findByPosition(value);
    discDrives.forEach(dd -> dd.add(selfRelLink(dd.getId())));
    return discDrives;
  }

  @GetMapping("/type/{value}")
  public List<DiscDrive> findByType(@PathVariable("value") StatTypes type) {
    List<DiscDrive> discDrives = repository.findByType(type);
    discDrives.forEach(dd -> dd.add(selfRelLink(dd.getId())));
    return discDrives;
  }

  @GetMapping("/base_value/{value}")
  public List<DiscDrive> findByBaseValue(@PathVariable float value) {
    List<DiscDrive> discDrives = repository.findByBaseValue(value);
    discDrives.forEach(dd -> dd.add(selfRelLink(dd.getId())));
    return discDrives;
  }

  @GetMapping("/base_value/{from}/{to}")
  public List<DiscDrive> findByBaseValueRange(@PathVariable float from, @PathVariable float to) {
    List<DiscDrive> discDrives = repository.findByBaseValueBetween(from, to);
    discDrives.forEach(dd -> dd.add(selfRelLink(dd.getId())));
    return discDrives;
  }

  @GetMapping("/set_id/{value}")
  public List<DiscDrive> findByDiscDriveSetId(@PathVariable String value) {
    List<DiscDrive> discDrives = repository.findByDiscDriveSetId(value);
    discDrives.forEach(dd -> dd.add(selfRelLink(dd.getId())));
    return discDrives;
  }

  @GetMapping("/disc_drive_id/{value}")
  public List<DiscDrive> findByDiscDriveId(@PathVariable String value) {
    List<DiscDrive> discDrives = repository.findByDiscDriveId(value);
    discDrives.forEach(dd -> dd.add(selfRelLink(dd.getId())));
    return discDrives;
  }

  @GetMapping("/position/{position}/type/{type}")
  public List<DiscDrive> findByPositionAndType(@PathVariable int position, @PathVariable("type") StatTypes type) {
    List<DiscDrive> discDrives = repository.findByPositionAndType(position, type);
    discDrives.forEach(dd -> dd.add(selfRelLink(dd.getId())));
    return discDrives;
  }

  @GetMapping("/set/{setId}/position/{position}")
  public List<DiscDrive> findBySetAndPosition(@PathVariable String setId, @PathVariable int position) {
    List<DiscDrive> discDrives = repository.findByDiscDriveSetIdAndPosition(setId, position);
    discDrives.forEach(dd -> dd.add(selfRelLink(dd.getId())));
    return discDrives;
  }

  @Override
  public List<DiscDrive> findByVersion(@PathVariable String version) {
    // DiscDrive entity does not have version field, return empty list
    return List.of();
  }

  @Override
  public List<DiscDrive> findByVersion(@PathVariable String from, @PathVariable String to) {
    // DiscDrive entity does not have version field, return empty list
    return List.of();
  }

}
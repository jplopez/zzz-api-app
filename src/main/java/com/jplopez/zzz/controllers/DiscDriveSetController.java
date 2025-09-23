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

import com.jplopez.zzz.entities.DiscDriveSet;
import com.jplopez.zzz.repositories.DiscDriveSetRepository;

/**
 * Rest Controller to retrieve data from Disc Drive Sets
 * 
 * @since 1.0
 */
@RestController
@RequestMapping(path = { "/discdrivesets", "/dds" })
public class DiscDriveSetController extends ZZZController<DiscDriveSet, DiscDriveSetRepository> {

  DiscDriveSetController(DiscDriveSetRepository repository) {
    super("discdrivesets", repository);
  }

  @Override
  @GetMapping(produces = { "application/hal+json" })
  public CollectionModel<DiscDriveSet> findAll() {
    List<DiscDriveSet> discDriveSets = repository.findAll();
    
    // Add self links to each disc drive set
    discDriveSets.forEach(dds -> dds.add(selfRelLink(dds.getId())));
    
    Link link = linkTo(DiscDriveSetController.class).withSelfRel();
    return CollectionModel.of(discDriveSets, link);
  }

  @Override
  @GetMapping("/{id}")
  public DiscDriveSet findOne(@PathVariable String id) {
    DiscDriveSet discDriveSet = super.findOne(id);
    discDriveSet.add(selfRelLink(discDriveSet.getId()));
    return discDriveSet;
  }

  @GetMapping("/name/{value}")
  public List<DiscDriveSet> findByName(@PathVariable String value) {
    List<DiscDriveSet> discDriveSets = repository.findByNameIgnoreCase(value);
    discDriveSets.forEach(dds -> dds.add(selfRelLink(dds.getId())));
    return discDriveSets;
  }

  @GetMapping("/name_like/{value}")
  public List<DiscDriveSet> findByNameLike(@PathVariable String value) {
    List<DiscDriveSet> discDriveSets = repository.findByNameContaining(value);
    discDriveSets.forEach(dds -> dds.add(selfRelLink(dds.getId())));
    return discDriveSets;
  }

  @GetMapping("/description_like/{value}")
  public List<DiscDriveSet> findByDescriptionLike(@PathVariable String value) {
    List<DiscDriveSet> discDriveSets = repository.findByDescriptionContaining(value);
    discDriveSets.forEach(dds -> dds.add(selfRelLink(dds.getId())));
    return discDriveSets;
  }

  @GetMapping("/two_piece_like/{value}")
  public List<DiscDriveSet> findByTwoPieceLike(@PathVariable String value) {
    List<DiscDriveSet> discDriveSets = repository.findByTwoPieceSkillDescriptionContaining(value);
    discDriveSets.forEach(dds -> dds.add(selfRelLink(dds.getId())));
    return discDriveSets;
  }

  @GetMapping("/four_piece_like/{value}")
  public List<DiscDriveSet> findByFourPieceLike(@PathVariable String value) {
    List<DiscDriveSet> discDriveSets = repository.findByFourPieceSkillDescriptionContaining(value);
    discDriveSets.forEach(dds -> dds.add(selfRelLink(dds.getId())));
    return discDriveSets;
  }

  @GetMapping("/set_id/{value}")
  public List<DiscDriveSet> findByDiscDriveSetId(@PathVariable String value) {
    List<DiscDriveSet> discDriveSets = repository.findByDiscDriveSetId(value);
    discDriveSets.forEach(dds -> dds.add(selfRelLink(dds.getId())));
    return discDriveSets;
  }

  @Override
  public List<DiscDriveSet> findByVersion(@PathVariable String version) {
    // DiscDriveSet entity does not have version field, return empty list
    return List.of();
  }

  @Override
  public List<DiscDriveSet> findByVersion(@PathVariable String from, @PathVariable String to) {
    // DiscDriveSet entity does not have version field, return empty list
    return List.of();
  }

}
package com.jplopez.zzz.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jplopez.zzz.common.exceptions.ZZZEntityNotFoundException;
import com.jplopez.zzz.entities.DiscDrive;
import com.jplopez.zzz.entities.DiscDriveSet;
import com.jplopez.zzz.repositories.DiscDriveRepository;
import com.jplopez.zzz.repositories.DiscDriveSetRepository;

/** Read-only catalog of Drive Disc sets and their bonuses. */
@RestController
@RequestMapping(path = { "/discdrivesets", "/dds", "/discsets" })
public class DiscDriveSetController extends ZZZController<DiscDriveSet, DiscDriveSetRepository> {

  private final DiscDriveRepository discsRepository;

  DiscDriveSetController(DiscDriveSetRepository repository, DiscDriveRepository discsRepository) {
    super("discdrivesets", repository);
    this.discsRepository = discsRepository;
  }

  @Override
  protected DiscDriveSet withLinks(DiscDriveSet set) {
    return set.add(selfRelLink(set.getId()), discsLink(set.getId()).withRel("discs"));
  }

  @GetMapping("/name/{value}")
  public List<DiscDriveSet> findByName(@PathVariable String value) {
    return withLinks(repository.findByNameIgnoreCase(value));
  }

  @GetMapping("/name_like/{value}")
  public List<DiscDriveSet> findByNameLike(@PathVariable String value) {
    return withLinks(repository.findByNameContainingIgnoreCase(value));
  }

  @GetMapping("/description_like/{value}")
  public List<DiscDriveSet> findByDescriptionLike(@PathVariable String value) {
    return withLinks(repository.findByDescriptionContainingIgnoreCase(value));
  }

  @GetMapping("/two_piece_skill_like/{value}")
  public List<DiscDriveSet> findByTwoPieceSkillLike(@PathVariable String value) {
    return withLinks(repository.findByTwoPieceSkillDescriptionContainingIgnoreCase(value));
  }

  @GetMapping("/four_piece_skill_like/{value}")
  public List<DiscDriveSet> findByFourPieceSkillLike(@PathVariable String value) {
    return withLinks(repository.findByFourPieceSkillDescriptionContainingIgnoreCase(value));
  }

  @GetMapping(value = "/{id}/discs", produces = MediaTypes.HAL_JSON_VALUE)
  public CollectionModel<DiscDrive> getDiscs(@PathVariable String id) {
    if (!repository.existsById(id)) {
      throw new ZZZEntityNotFoundException();
    }
    List<DiscDrive> discs = discsRepository.findByDiscDriveSetIdOrderByPositionAscIdAsc(id).stream()
        .map(DiscDriveController::addDiscLinks)
        .toList();
    return CollectionModel.of(discs, discsLink(id).withSelfRel(), selfRelLink(id).withRel("set"));
  }

  private static WebMvcLinkBuilder discsLink(String id) {
    return linkTo(methodOn(DiscDriveSetController.class).getDiscs(id));
  }
}

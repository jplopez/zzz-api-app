package com.jplopez.zzz.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.jplopez.zzz.common.exceptions.ZZZEntityNotFoundException;
import com.jplopez.zzz.dto.StatProgression;
import com.jplopez.zzz.entities.Bangboo;
import com.jplopez.zzz.entities.BangbooStat;
import com.jplopez.zzz.entities.Skill;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.repositories.BangbooRepository;
import com.jplopez.zzz.repositories.BangbooStatRepository;
import com.jplopez.zzz.repositories.ProgressionRepository.Owner;
import com.jplopez.zzz.services.ProgressionService;

/** Read-only Bangboo catalog, stats, and skills. */
@RestController
@RequestMapping(path = { "/bangboos", "/b", "/bangboo" })
public class BangbooController extends ZZZController<Bangboo, BangbooRepository> {

  private final BangbooStatRepository statsRepository;
  private final ProgressionService progression;

  BangbooController(BangbooRepository repository, BangbooStatRepository statsRepository,
                    ProgressionService progression) {
    super("bangboos", repository);
    this.statsRepository = statsRepository;
    this.progression = progression;
  }

  @Override
  protected Bangboo withLinks(Bangboo bangboo) {
    String id = bangboo.getId();
    return bangboo.add(selfRelLink(id),
        linkTo(methodOn(BangbooController.class).getBangbooStats(id)).withRel("stats"),
        linkTo(BangbooController.class).slash(id).slash("stats").slash("base").withRel("baseStats")
            .withType(MediaType.APPLICATION_JSON_VALUE),
        linkTo(methodOn(BangbooController.class).getSkillsForBangboo(id)).withRel("allSkills"));
  }

  @GetMapping("/name/{value}")
  public List<Bangboo> findByName(@PathVariable String value) {
    return withLinks(repository.findByNameIgnoreCase(value));
  }

  @GetMapping("/name_like/{value}")
  public List<Bangboo> findByNameLike(@PathVariable String value) {
    return withLinks(repository.findByNameContaining(value));
  }

  @GetMapping("/rarity/{value}")
  public List<Bangboo> findByRarity(@PathVariable Rarity value) {
    return withLinks(repository.findByRarity(value));
  }

  @GetMapping("/faction_like/{value}")
  public List<Bangboo> findByFaction(@PathVariable String value) {
    return withLinks(repository.findByFactionContaining(value));
  }

  @GetMapping("/version/{value}")
  public List<Bangboo> findByVersion(@PathVariable float value) {
    validateVersion(value);
    return withLinks(repository.findByVersion(value));
  }

  @GetMapping("/version/{from}/{to}")
  public List<Bangboo> findByVersion(@PathVariable float from, @PathVariable float to) {
    validateVersion(from);
    validateVersion(to);
    validateRange(from, to);
    return withLinks(repository.findByVersionBetween(from, to));
  }

  // Numeric single segments remain legacy level lookups; all others are stat categories.
  @GetMapping(value = { "/{id}/stats/{statType:(?![+-]?[0-9]+$).+}", "/{id}/stats/{statType}/{level}" },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public StatProgression getStats(@PathVariable String id, @PathVariable String statType,
                                  @PathVariable(required = false) Integer level,
                                  @RequestParam(required = false) String statId) {
    return progression.stats(Owner.BANGBOO, id, statType, level, statId);
  }

  @GetMapping(value = "/{id}/skills", produces = MediaTypes.HAL_JSON_VALUE)
  public CollectionModel<Skill> getSkillsForBangboo(@PathVariable String id) {
    List<Skill> skills = progression.skills(Owner.BANGBOO, id).stream().map(skill -> withSkillLinks(id, skill)).toList();
    return CollectionModel.of(skills,
        linkTo(methodOn(BangbooController.class).getSkillsForBangboo(id)).withSelfRel());
  }

  @GetMapping("/{id}/skills/{skillId}")
  public Skill getSkill(@PathVariable String id, @PathVariable String skillId) {
    return withSkillLinks(id, progression.skill(Owner.BANGBOO, id, skillId));
  }

  @GetMapping(value = { "/{id}/skills/{skillId}/stats", "/{id}/skills/{skillId}/stats/{level}" },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public StatProgression getSkillStats(@PathVariable String id, @PathVariable String skillId,
                                       @PathVariable(required = false) Integer level,
                                       @RequestParam(required = false) String statId) {
    return progression.skillStats(Owner.BANGBOO, id, skillId, level, statId);
  }

  private static Skill withSkillLinks(String bangbooId, Skill skill) {
    var skillLink = linkTo(methodOn(BangbooController.class).getSkill(bangbooId, skill.getId()));
    return skill.add(skillLink.withSelfRel(), skillLink.slash("stats").withRel("stats").withType(MediaType.APPLICATION_JSON_VALUE));
  }

  @GetMapping("/{id}/stats")
  public List<BangbooStat> getBangbooStats(@PathVariable String id) {
    requireBangboo(id);
    return statsRepository.findByBangbooIdOrderByLevelAsc(id).stream()
        .map(stat -> stat.add(statLink(id, stat.getLevel()).withSelfRel()))
        .toList();
  }

  @GetMapping("/{id}/stats/{level:[+-]?[0-9]+}")
  public BangbooStat getBangbooStatByLevel(@PathVariable String id, @PathVariable int level) {
    if (level < 1 || level > 60) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bangboo level must be between 1 and 60");
    }
    requireBangboo(id);
    BangbooStat stat = statsRepository.findByBangbooIdAndLevel(id, level)
        .orElseThrow(ZZZEntityNotFoundException::new);
    stat.add(statLink(id, level).withSelfRel(),
        linkTo(methodOn(BangbooController.class).getBangbooStats(id)).withRel("allStats"));

    if (level > 1 && statsRepository.existsByBangbooIdAndLevel(id, level - 1)) {
      stat.add(statLink(id, level - 1).withRel("prev"));
    }
    if (level < 60 && statsRepository.existsByBangbooIdAndLevel(id, level + 1)) {
      stat.add(statLink(id, level + 1).withRel("next"));
    }
    return stat;
  }

  private void requireBangboo(String id) {
    if (!repository.existsById(id)) {
      throw new ZZZEntityNotFoundException();
    }
  }

  private static WebMvcLinkBuilder statLink(String id, int level) {
    return linkTo(methodOn(BangbooController.class).getBangbooStatByLevel(id, level));
  }
}

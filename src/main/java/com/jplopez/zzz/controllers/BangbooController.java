package com.jplopez.zzz.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.jplopez.zzz.common.exceptions.ZZZEntityNotFoundException;
import com.jplopez.zzz.entities.Bangboo;
import com.jplopez.zzz.entities.BangbooStat;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.repositories.BangbooRepository;
import com.jplopez.zzz.repositories.BangbooStatRepository;

/** Read-only Bangboo catalog and level stats. */
@RestController
@RequestMapping(path = { "/bangboos", "/b" })
public class BangbooController extends ZZZController<Bangboo, BangbooRepository> {

  private final BangbooStatRepository statsRepository;

  BangbooController(BangbooRepository repository, BangbooStatRepository statsRepository) {
    super("bangboos", repository);
    this.statsRepository = statsRepository;
  }

  @Override
  protected Bangboo withLinks(Bangboo bangboo) {
    String id = bangboo.getId();
    return bangboo.add(selfRelLink(id),
        linkTo(methodOn(BangbooController.class).getBangbooStats(id)).withRel("stats"));
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

  @GetMapping("/{id}/stats")
  public List<BangbooStat> getBangbooStats(@PathVariable String id) {
    requireBangboo(id);
    return statsRepository.findByBangbooIdOrderByLevelAsc(id).stream()
        .map(stat -> stat.add(statLink(id, stat.getLevel()).withSelfRel()))
        .toList();
  }

  @GetMapping("/{id}/stats/{level}")
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

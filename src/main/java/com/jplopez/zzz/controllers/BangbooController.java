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

import com.jplopez.zzz.common.exceptions.ZZZEntityNotFoundException;
import com.jplopez.zzz.entities.Bangboo;
import com.jplopez.zzz.entities.BangbooStat;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.repositories.BangbooRepository;
import com.jplopez.zzz.repositories.BangbooStatRepository;

/**
 * Rest Controller to retrieve data from Bangboos
 * 
 * @since 1.0
 */
@RestController
@RequestMapping(path = { "/bangboos", "/b" })
public class BangbooController extends ZZZController<Bangboo, BangbooRepository> {

  private final BangbooStatRepository bangbooStatRepository;

  BangbooController(BangbooRepository repository, BangbooStatRepository bangbooStatRepository) {
      super("bangboos", repository);
      this.bangbooStatRepository = bangbooStatRepository;
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

  /**
   * Get all stats for a specific bangboo ordered by level
   * @param id the bangboo ID
   * @return list of stats ordered by level ascending
   */
  @GetMapping("/{id}/stats")
  public List<BangbooStat> getBangbooStats(@PathVariable String id) {
    // Verify bangboo exists
    repository.findById(id)
        .orElseThrow(ZZZEntityNotFoundException::new);

    List<BangbooStat> stats = bangbooStatRepository.findByBangbooIdOrderByLevelAsc(id);
    
    // Add HATEOAS links to each stat
    for(BangbooStat stat : stats) {
      stat.add(linkTo(methodOn(BangbooController.class).getBangbooStatByLevel(id, stat.getLevel())).withSelfRel());
    }
    
    return stats;
  }

  /**
   * Get stats for a specific bangboo at a specific level with HATEOAS navigation links
   * @param id the bangboo ID
   * @param level the level (1-60)
   * @return bangboo stat with navigation links
   */
  @GetMapping("/{id}/stats/{level}")
  public BangbooStat getBangbooStatByLevel(@PathVariable String id, @PathVariable int level) {
    // Verify bangboo exists
    repository.findById(id)
        .orElseThrow(ZZZEntityNotFoundException::new);

    BangbooStat stat = bangbooStatRepository.findByBangbooIdAndLevel(id, level)
        .orElseThrow(ZZZEntityNotFoundException::new);

    // Add self link
    stat.add(linkTo(methodOn(BangbooController.class).getBangbooStatByLevel(id, level)).withSelfRel());
    
    // Add link to all stats
    stat.add(linkTo(methodOn(BangbooController.class).getBangbooStats(id)).withRel("allStats"));
    
    // Add previous level link if not level 1
    if (level > 1) {
      stat.add(linkTo(methodOn(BangbooController.class).getBangbooStatByLevel(id, level - 1)).withRel("prev"));
    }
    
    // Add next level link if not level 60
    if (level < 60) {
      stat.add(linkTo(methodOn(BangbooController.class).getBangbooStatByLevel(id, level + 1)).withRel("next"));
    }
    
    return stat;
  }
}
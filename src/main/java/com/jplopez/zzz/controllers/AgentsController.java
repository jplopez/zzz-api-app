package com.jplopez.zzz.controllers;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.jplopez.zzz.common.exceptions.NotFoundException;
import com.jplopez.zzz.entities.Agent;
import com.jplopez.zzz.entities.Skill;
import com.jplopez.zzz.entities.enums.Attributes;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialities;
import com.jplopez.zzz.entities.enums.Type;
import com.jplopez.zzz.repositories.AgentsRepository;
import com.jplopez.zzz.repositories.SkillsRepository;

/**
 * Rest Controller to retrieve data from Agents
 * 
 * @since 1.0
 */
@RestController
@RequestMapping(path = { "/agents", "/a" })
public class AgentsController {

  private final AgentsRepository repository;

  private final SkillsRepository skillsRepo;

  AgentsController(AgentsRepository repository, SkillsRepository skillsRepo) {
      this.repository = repository;
      this.skillsRepo = skillsRepo;
  }

  @GetMapping(produces = { "application/hal+json" })
  public CollectionModel<Agent> findAll() {
    List<Agent> agents = repository.findAll();

    //TODO extract HATEOAS-ization of Agent to a method so i can reuse in all controller methods
    for(Agent a : agents) {
      String agentId = a.getAgentId();
      Link selfLink = linkTo(AgentsController.class).slash(agentId).withSelfRel();
      a.add(selfLink);

      if(!skillsRepo.findByAgentId(agentId).isEmpty()) {
        Link ordersLink = linkTo(methodOn(AgentsController.class)
          .getSkillsforAgent(agentId)).withRel("allSkills");
        a.add(ordersLink);
      }
    }
    Link link = linkTo(AgentsController.class).withSelfRel();
    return CollectionModel.of(agents, link);
  }

  @GetMapping("/{id}")
  public Agent findOne(@PathVariable String id) {
    return repository.findById(id)
        .orElseThrow(NotFoundException::new);
  }

  @GetMapping("/name/{value}")
  public List<Agent> findByName(@PathVariable String value) {
    return repository.findByNameIgnoreCase(value);
  }

  @GetMapping("/name_like/{value}")
  public List<Agent> findByNameLike(@PathVariable String value) {
    return repository.findByFullNameContaining(value);
  }

  @GetMapping("/rarity/{value}")
  public List<Agent> findByRarity(@PathVariable("value") Rarity rarity) {
    return repository.findByRarity(rarity);
  }

  @GetMapping("/attribute/{value}")
  public List<Agent> findByAttribute(@PathVariable("value") Attributes attr) {
    return repository.findByAttribute(attr);
  }

  @GetMapping("/speciality/{value}")
  public List<Agent> findBySpeciality(@PathVariable("value") Specialities spec) {
    return repository.findBySpeciality(spec);
  }

  @GetMapping("/type/{value}")
  public List<Agent> findByType(@PathVariable("value") Type typ) {
    return repository.findByType(typ);
  }

  @GetMapping("/faction_like/{value}")
  public List<Agent> findByFaction(@PathVariable("value") String value) {
    return repository.findByFactionContaining(value);
  }

  @GetMapping("/version/{value}")
  public List<Agent> findByVersion(@PathVariable String value) {
    return repository.findByVersion(Double.valueOf(value));
  }

  @GetMapping("/version/{from}/{to}")
  public List<Agent> findByVersion(@PathVariable String from, @PathVariable String to) {
    return repository.findByVersionBetween(Double.valueOf(from), Double.valueOf(to));
  }

  @GetMapping("/{agentId}/skill/{skillId}")
  public Skill findSkillById(@PathVariable final String agentId, @PathVariable final String skillId) {
    return skillsRepo.findByIdAndAgentId(agentId, skillId).orElseThrow(NotFoundException::new);
  }

  @GetMapping(value="/{agentId}/skills", produces = {"application/hal+json" })
  public CollectionModel<Skill> getSkillsforAgent(@PathVariable final String agentId) {
    List<Skill> skills = skillsRepo.findByAgentId(agentId);
    for(final Skill skill : skills) {
      Link selfLink = linkTo(methodOn(AgentsController.class)
          .findSkillById(agentId, skill.getId())).withSelfRel();
      skill.add(selfLink);
    }

    Link link = linkTo(methodOn(AgentsController.class)
      .getSkillsforAgent(agentId)).withSelfRel();
    return CollectionModel.of(skills, link);
  }
}

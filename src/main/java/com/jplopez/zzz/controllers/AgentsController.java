package com.jplopez.zzz.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.jplopez.zzz.common.exceptions.ZZZEntityNotFoundException;
import com.jplopez.zzz.entities.Agent;
import com.jplopez.zzz.entities.AgentSkill;
import com.jplopez.zzz.entities.Skill;
import com.jplopez.zzz.entities.SkillStat;
import com.jplopez.zzz.entities.SkillWithStats;
import com.jplopez.zzz.entities.enums.Attributes;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialties;
import com.jplopez.zzz.entities.enums.Type;
import com.jplopez.zzz.repositories.AgentSkillRepository;
import com.jplopez.zzz.repositories.AgentsRepository;
import com.jplopez.zzz.repositories.SkillStatRepository;
import com.jplopez.zzz.repositories.SkillsRepository;


/**
 * Rest Controller to retrieve data from Agents
 * 
 * @since 1.0
 */
@RestController
@RequestMapping(path = { "/agents", "/a" })
public class AgentsController extends ZZZController<Agent,AgentsRepository> {

  private final SkillsRepository skillsRepo;
  private final AgentSkillRepository agentSkillRepo;
  private final SkillStatRepository skillStatRepo;

  AgentsController(AgentsRepository repository, SkillsRepository skillsRepo, 
                   AgentSkillRepository agentSkillRepo, SkillStatRepository skillStatRepo) {
      super("agents", repository);
      this.skillsRepo = skillsRepo;
      this.agentSkillRepo = agentSkillRepo;
      this.skillStatRepo = skillStatRepo;
  }


  @Override
  @GetMapping(produces = { "application/hal+json" })
  public CollectionModel<Agent> findAll() {
    List<Agent> agents = repository.findAll();

    //TODO extract HATEOAS-ization of Agent to a method so i can reuse in all controller methods
    for(Agent a : agents) {
      String agentId = a.getAgentId();
      Link selfLink = linkTo(AgentsController.class).slash(agentId).withSelfRel();
      a.add(selfLink);

      if(!agentSkillRepo.findByAgentId(agentId).isEmpty()) {
        Link ordersLink = linkTo(methodOn(AgentsController.class)
          .getSkillsforAgent(agentId)).withRel("allSkills");
        a.add(ordersLink);
      }
    }
    Link link = linkTo(AgentsController.class).withSelfRel();
    return CollectionModel.of(agents, link);
  }

  @Override
  @GetMapping("/{id}")
  public Agent findOne(@PathVariable String id) {
    return repository.findById(id)
        .orElseThrow(ZZZEntityNotFoundException::new);
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
  public List<Agent> findBySpeciality(@PathVariable("value") Specialties spec) {
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
    return repository.findByVersion(Float.valueOf(value));
  }


  @GetMapping("/version/{from}/{to}")
  public List<Agent> findByVersion(@PathVariable String from, @PathVariable String to) {
    return repository.findByVersionBetween(Float.valueOf(from), Float.valueOf(to));
  }

  @GetMapping("/{agentId}/skill/{skillId}")
  public SkillWithStats findSkillById(@PathVariable final String agentId, @PathVariable final String skillId) {
    // Verify the agent-skill relationship exists
    agentSkillRepo.findByAgentIdAndSkillId(agentId, skillId)
      .orElseThrow(ZZZEntityNotFoundException::new);
    
    // Get the skill
    Skill skill = skillsRepo.findById(skillId)
      .orElseThrow(ZZZEntityNotFoundException::new);
    
    // Get all skill stats for this skill
    List<SkillStat> skillStats = skillStatRepo.findBySkillId(skillId);
    
    return new SkillWithStats(skill, skillStats);
  }

  @GetMapping(value="/{agentId}/skills", produces = {"application/hal+json" })
  public CollectionModel<Skill> getSkillsforAgent(@PathVariable final String agentId) {
    // Get agent-skill relationships
    List<AgentSkill> agentSkills = agentSkillRepo.findByAgentId(agentId);
    
    // Get the actual skills
    List<Skill> skills = agentSkills.stream()
      .map(as -> skillsRepo.findById(as.getSkillId()))
      .filter(opt -> opt.isPresent())
      .map(opt -> opt.get())
      .collect(Collectors.toList());
    
    for(final Skill skill : skills) {
      Link selfLink = linkTo(methodOn(AgentsController.class)
          .findSkillById(agentId, skill.getId())).withSelfRel();
      skill.add(selfLink);
      
      // Add link to skill stats
      Link skillStatsLink = linkTo(methodOn(AgentsController.class)
          .getSkillWithStats(agentId, skill.getId())).withRel("skillStats");
      skill.add(skillStatsLink);
    }

    Link link = linkTo(methodOn(AgentsController.class)
      .getSkillsforAgent(agentId)).withSelfRel();
    return CollectionModel.of(skills, link);
  }

  @GetMapping("/{agentId}/skills/{skillId}")
  public SkillWithStats getSkillWithStats(@PathVariable final String agentId, @PathVariable final String skillId) {
    // Verify the agent-skill relationship exists
    agentSkillRepo.findByAgentIdAndSkillId(agentId, skillId)
      .orElseThrow(ZZZEntityNotFoundException::new);
    
    // Get the skill
    Skill skill = skillsRepo.findById(skillId)
      .orElseThrow(ZZZEntityNotFoundException::new);
    
    // Get all skill stats for this skill
    List<SkillStat> skillStats = skillStatRepo.findBySkillId(skillId);
    
    SkillWithStats skillWithStats = new SkillWithStats(skill, skillStats);
    
    // Add HATEOAS links
    Link selfLink = linkTo(methodOn(AgentsController.class)
        .getSkillWithStats(agentId, skillId)).withSelfRel();
    skillWithStats.add(selfLink);
    
    return skillWithStats;
  }

  @GetMapping("/{agentId}/skills/{skillId}/level/{level}")
  public SkillWithStats getSkillWithStatsForLevel(@PathVariable final String agentId, 
                                                  @PathVariable final String skillId,
                                                  @PathVariable final int level) {
    // Verify the agent-skill relationship exists
    agentSkillRepo.findByAgentIdAndSkillId(agentId, skillId)
      .orElseThrow(ZZZEntityNotFoundException::new);
    
    // Get the skill
    Skill skill = skillsRepo.findById(skillId)
      .orElseThrow(ZZZEntityNotFoundException::new);
    
    // Get skill stats for specific level
    List<SkillStat> skillStats = skillStatRepo.findBySkillIdAndLevel(skillId, level);
    
    SkillWithStats skillWithStats = new SkillWithStats(skill, skillStats);
    
    // Add HATEOAS links
    Link selfLink = linkTo(methodOn(AgentsController.class)
        .getSkillWithStatsForLevel(agentId, skillId, level)).withSelfRel();
    skillWithStats.add(selfLink);
    
    return skillWithStats;
  }

}

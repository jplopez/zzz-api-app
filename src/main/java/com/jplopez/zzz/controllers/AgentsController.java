package com.jplopez.zzz.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jplopez.zzz.dto.StatProgression;
import com.jplopez.zzz.entities.Agent;
import com.jplopez.zzz.entities.Skill;
import com.jplopez.zzz.entities.SkillStat;
import com.jplopez.zzz.entities.SkillWithStats;
import com.jplopez.zzz.entities.enums.Attributes;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialties;
import com.jplopez.zzz.entities.enums.Type;
import com.jplopez.zzz.repositories.AgentsRepository;
import com.jplopez.zzz.repositories.ProgressionRepository.Owner;
import com.jplopez.zzz.repositories.SkillStatRepository;
import com.jplopez.zzz.services.ProgressionService;

/** Read-only Agent catalog, stats, and skills. */
@RestController
@RequestMapping(path = { "/agents", "/a", "/agent" })
public class AgentsController extends ZZZController<Agent, AgentsRepository> {

  private final ProgressionService progression;
  private final SkillStatRepository skillStatRepo;

  AgentsController(AgentsRepository repository, ProgressionService progression, SkillStatRepository skillStatRepo) {
    super("agents", repository);
    this.progression = progression;
    this.skillStatRepo = skillStatRepo;
  }

  @Override
  protected Agent withLinks(Agent agent) {
    String id = agent.getId();
    return agent.add(selfRelLink(id),
        linkTo(AgentsController.class).slash(id).slash("stats").slash("base").withRel("stats")
            .withType(MediaType.APPLICATION_JSON_VALUE),
        linkTo(methodOn(AgentsController.class).getSkillsForAgent(id)).withRel("allSkills"));
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

  @GetMapping(value = { "/{id}/stats/{statType}", "/{id}/stats/{statType}/{level}" },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public StatProgression getStats(@PathVariable String id, @PathVariable String statType,
                                  @PathVariable(required = false) Integer level,
                                  @RequestParam(required = false) String statId) {
    return progression.stats(Owner.AGENT, id, statType, level, statId);
  }

  @GetMapping(value = "/{agentId}/skills", produces = MediaTypes.HAL_JSON_VALUE)
  public CollectionModel<Skill> getSkillsForAgent(@PathVariable String agentId) {
    List<Skill> skills = progression.skills(Owner.AGENT, agentId);
    for (Skill skill : skills) {
      var skillLink = linkTo(methodOn(AgentsController.class).getSkillWithStats(agentId, skill.getId()));
      skill.add(linkTo(methodOn(AgentsController.class).findSkillById(agentId, skill.getId())).withSelfRel(),
          skillLink.withRel("skillStats"),
          skillLink.slash("stats").withRel("stats").withType(MediaType.APPLICATION_JSON_VALUE));
    }
    return CollectionModel.of(skills,
        linkTo(methodOn(AgentsController.class).getSkillsForAgent(agentId)).withSelfRel());
  }

  @GetMapping(value = { "/{id}/skills/{skillId}/stats", "/{id}/skills/{skillId}/stats/{level}" },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public StatProgression getSkillStats(@PathVariable String id, @PathVariable String skillId,
                                       @PathVariable(required = false) Integer level,
                                       @RequestParam(required = false) String statId) {
    return progression.skillStats(Owner.AGENT, id, skillId, level, statId);
  }

  @GetMapping("/{agentId}/skill/{skillId}")
  public SkillWithStats findSkillById(@PathVariable String agentId, @PathVariable String skillId) {
    return legacySkillWithStats(agentId, skillId, null);
  }

  @GetMapping("/{agentId}/skills/{skillId}")
  public SkillWithStats getSkillWithStats(@PathVariable String agentId, @PathVariable String skillId) {
    return legacySkillWithStats(agentId, skillId, null)
        .add(linkTo(methodOn(AgentsController.class).getSkillWithStats(agentId, skillId)).withSelfRel());
  }

  @GetMapping("/{agentId}/skills/{skillId}/level/{level}")
  public SkillWithStats getSkillWithStatsForLevel(@PathVariable String agentId, @PathVariable String skillId,
                                                 @PathVariable int level) {
    return legacySkillWithStats(agentId, skillId, level)
        .add(linkTo(methodOn(AgentsController.class).getSkillWithStatsForLevel(agentId, skillId, level)).withSelfRel());
  }

  private SkillWithStats legacySkillWithStats(String agentId, String skillId, Integer level) {
    Skill skill = progression.skill(Owner.AGENT, agentId, skillId);
    List<SkillStat> stats = level == null ? skillStatRepo.findBySkillId(skill.getId())
        : skillStatRepo.findBySkillIdAndLevel(skill.getId(), level);
    return new SkillWithStats(skill, stats);
  }
}

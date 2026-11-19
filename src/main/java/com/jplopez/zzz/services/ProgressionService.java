package com.jplopez.zzz.services;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.jplopez.zzz.common.exceptions.ZZZEntityNotFoundException;
import com.jplopez.zzz.dto.StatProgression;
import com.jplopez.zzz.dto.StatProgression.LevelValue;
import com.jplopez.zzz.dto.StatProgression.Stat;
import com.jplopez.zzz.entities.Skill;
import com.jplopez.zzz.repositories.ProgressionRepository;
import com.jplopez.zzz.repositories.ProgressionRepository.Owner;
import com.jplopez.zzz.repositories.ProgressionRepository.StatRow;

@Service
public class ProgressionService {

  private static final Set<String> AGENT_TYPES = Set.of(
      "base", "basic", "dodge", "assist", "ultimate", "chain", "special", "ex", "dash", "counter");
  private static final Set<String> BANGBOO_TYPES = Set.of("base", "active", "additional", "chain");

  private final ProgressionRepository repository;

  public ProgressionService(ProgressionRepository repository) {
    this.repository = repository;
  }

  public StatProgression stats(Owner owner, String ownerId, String type, Integer level, String statId) {
    String category = type.toLowerCase(Locale.ROOT);
    Set<String> allowedTypes = owner == Owner.AGENT ? AGENT_TYPES : BANGBOO_TYPES;
    if (!allowedTypes.contains(category)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid stat type for this character");
    }
    validateSelection(level, statId);
    requireOwner(owner, ownerId);
    return assemble(repository.findStats(owner, ownerId, category, level, statId), statId);
  }

  public List<Skill> skills(Owner owner, String ownerId) {
    requireOwner(owner, ownerId);
    return repository.findSkills(owner, ownerId);
  }

  public Skill skill(Owner owner, String ownerId, String skillId) {
    return repository.findSkill(owner, ownerId, skillId).orElseThrow(ZZZEntityNotFoundException::new);
  }

  public StatProgression skillStats(Owner owner, String ownerId, String skillId, Integer level, String statId) {
    validateSelection(level, statId);
    Skill skill = skill(owner, ownerId, skillId);
    return assemble(repository.findSkillStats(skill.getId(), level, statId), statId);
  }

  private void requireOwner(Owner owner, String ownerId) {
    if (!repository.ownerExists(owner, ownerId)) {
      throw new ZZZEntityNotFoundException();
    }
  }

  private static void validateSelection(Integer level, String statId) {
    if (level != null && (level < 1 || level > 60)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Level must be between 1 and 60");
    }
    if (statId != null && statId.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "statId must not be blank");
    }
  }

  private static StatProgression assemble(List<StatRow> rows, String statId) {
    if (statId != null && rows.isEmpty()) {
      throw new ZZZEntityNotFoundException();
    }
    var groups = rows.stream().collect(groupingBy(StatRow::id, LinkedHashMap::new, toList()));
    return new StatProgression(groups.values().stream().map(ProgressionService::toStat).toList());
  }

  private static Stat toStat(List<StatRow> rows) {
    StatRow definition = rows.get(0);
    List<LevelValue> values = rows.stream().filter(row -> row.level() != null)
        .map(row -> new LevelValue(row.level(), row.amount()))
        .toList();
    return new Stat(definition.id(), definition.type(), definition.name(), definition.abbr(), definition.description(),
        definition.unit(), definition.skillId(), definition.tokenPosition(), values);
  }
}

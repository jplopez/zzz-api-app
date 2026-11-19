package com.jplopez.zzz.repositories;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.jplopez.zzz.common.exceptions.ZZZEntityNotFoundException;
import com.jplopez.zzz.entities.Skill;

/** Reads stat definitions and level values together, without a query per stat. */
@Repository
public class ProgressionRepository {

  // The only identifiers interpolated into queries come from this closed set.
  public enum Owner {
    AGENT("agent"), BANGBOO("bangboo");

    private final String table;

    Owner(String table) {
      this.table = table;
    }

    private Object key(String id) {
      return this == AGENT ? numericId(id) : id;
    }
  }

  public record StatRow(String id, String type, String name, String abbr, String description, String unit,
                        String skillId, Integer tokenPosition, Integer level, BigDecimal amount) {}

  private static final String SKILLS_QUERY = """
      select s.id, s.type, s.name, s.description
      from skill s join %1$s_skill membership on membership.skill_id = s.id
      where membership.%1$s_id = :ownerId
      """;

  private static final String STATS_QUERY = """
      select d.id, coalesce(lower(s.type), 'base') as type, d.name, d.abbr, d.description, d.unit,
             d.skill_id, d.token_position, v.level, v.amount
      from stat_definition d
      left join skill s on s.id = d.skill_id
      left join stat_value v on v.stat_id = d.id %s
      where %s
      order by d.id, v.level
      """;

  private final JdbcClient jdbc;

  public ProgressionRepository(JdbcClient jdbc) {
    this.jdbc = jdbc;
  }

  public boolean ownerExists(Owner owner, String ownerId) {
    return jdbc.sql("select exists(select 1 from %s where id = :id)".formatted(owner.table))
        .param("id", owner.key(ownerId))
        .query(Boolean.class).single();
  }

  public List<Skill> findSkills(Owner owner, String ownerId) {
    return jdbc.sql(SKILLS_QUERY.formatted(owner.table) + "order by s.id")
        .param("ownerId", owner.key(ownerId))
        .query(Skill.class).list();
  }

  public Optional<Skill> findSkill(Owner owner, String ownerId, String skillId) {
    return jdbc.sql(SKILLS_QUERY.formatted(owner.table) + "and s.id = :skillId")
        .param("ownerId", owner.key(ownerId))
        .param("skillId", numericId(skillId))
        .query(Skill.class).optional();
  }

  public List<StatRow> findStats(Owner owner, String ownerId, String type, Integer level, String statId) {
    if ("base".equals(type)) {
      return queryStats("d.%s_id = :ownerId".formatted(owner.table),
          Map.of("ownerId", owner.key(ownerId)), level, statId);
    }
    String predicate = """
        lower(s.type) = :type and exists (
          select 1 from %1$s_skill membership
          where membership.%1$s_id = :ownerId and membership.skill_id = d.skill_id
        )
        """.formatted(owner.table);
    return queryStats(predicate, Map.of("ownerId", owner.key(ownerId), "type", type), level, statId);
  }

  public List<StatRow> findSkillStats(String skillId, Integer level, String statId) {
    return queryStats("d.skill_id = :skillId", Map.of("skillId", numericId(skillId)), level, statId);
  }

  private List<StatRow> queryStats(String predicate, Map<String, Object> bindings, Integer level, String statId) {
    Map<String, Object> params = new HashMap<>(bindings);
    String valuePredicate = "";
    if (level != null) {
      // Filter in the join so a known stat without a value at this level is still returned.
      valuePredicate = "and v.level = :level";
      params.put("level", level);
    }
    if (statId != null) {
      predicate += " and d.id = :statId";
      params.put("statId", statId);
    }
    return jdbc.sql(STATS_QUERY.formatted(valuePredicate, predicate)).params(params).query(StatRow.class).list();
  }

  private static int numericId(String id) {
    try {
      return Integer.parseInt(id);
    } catch (NumberFormatException e) {
      // Agents and skills currently have integer database keys, exposed as strings in the API.
      throw new ZZZEntityNotFoundException();
    }
  }
}

package com.jplopez.zzz.entities;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.jplopez.zzz.entities.converter.RarityConverter;
import com.jplopez.zzz.entities.converter.SpecialityConverter;
import com.jplopez.zzz.entities.converter.StatTypeConverter;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialties;
import com.jplopez.zzz.entities.enums.StatTypes;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** A W-Engine and its catalog stats. */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "wengine")
@Relation(collectionRelation = "wengineList")
public class WEngine extends RepresentationModel<WEngine> {

  public WEngine(String id, String wengineId, String name) {
    this.id = id;
    this.wengineId = wengineId;
    this.name = name;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Exclude
  private String id;

  @Column(nullable = false, unique = true)
  private String wengineId;

  @Column(nullable = false, unique = true)
  private String name;

  private String description;

  @Convert(converter = RarityConverter.class)
  private Rarity rarity;

  private Integer baseAttack;

  @Convert(converter = SpecialityConverter.class)
  private Specialties specialty;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "wengine_skill_descriptions", joinColumns = @JoinColumn(name = "wengine_id"))
  @Column(name = "skill_descriptions", nullable = false)
  private List<String> skillDescriptions;

  @Convert(converter = StatTypeConverter.class)
  private StatTypes subStatType;

  private Float subStatBaseValue;

  @Column(nullable = false)
  private Float version = 1.0f;
}

package com.jplopez.zzz.entities;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import com.jplopez.zzz.entities.converter.RarityConverter;
import com.jplopez.zzz.entities.converter.SpecialityConverter;
import com.jplopez.zzz.entities.converter.StatTypeConverter;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialties;
import com.jplopez.zzz.entities.enums.StatTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * WEngine Entity.
 * 
 * Entities are mapped to tables (or equivalent) to transport the data received in REST controllers to the persistence layer (repositories).
 * Similarly, Entities take the results from querying DBs to the REST controllers who parse them to JSON and response to clients. 
 * 
 * @since 1.0
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class WEngine extends RepresentationModel<WEngine>{

  public WEngine(String id, String wengineId, String name) {
    this.id = id;
    this.wengineId = wengineId;
    this.name = name;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Exclude
  private String id;

  @Column(nullable = false, unique = true)
  private String wengineId;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = true)
  private String description;

  @Column(nullable = true)
  @Convert(converter = RarityConverter.class)
  private Rarity rarity;

  @Column(nullable = true)
  private Integer baseAttack;

  @Column(nullable = true)
  @Convert(converter = SpecialityConverter.class)
  private Specialties specialty;

  @ElementCollection(fetch = FetchType.EAGER)
  private List<String> skillDescriptions;

  @Column(nullable = true)
  @Convert(converter = StatTypeConverter.class)
  private StatTypes subStatType;

  @Column(nullable = true)
  private float subStatBaseValue;


}
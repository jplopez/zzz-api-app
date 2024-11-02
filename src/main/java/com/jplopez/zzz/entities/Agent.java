package com.jplopez.zzz.entities;

import com.jplopez.zzz.entities.converter.AttributesConverter;
import com.jplopez.zzz.entities.converter.RarityConverter;
import com.jplopez.zzz.entities.converter.SpecialityConverter;
import com.jplopez.zzz.entities.converter.TypeConverter;
import com.jplopez.zzz.entities.enums.Attributes;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialities;
import com.jplopez.zzz.entities.enums.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class Agent {

  public Agent(String id, String agentId, String name) {
    this.id = id;
    this.agentId = agentId;
    this.name = name;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Exclude
  private String id;

  @Column(nullable = false, unique = true)
  private String agentId;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String fullName;

  @Column(nullable = true)
  @Convert(converter = RarityConverter.class)
  private Rarity rarity;

  @Column(nullable = true)
  @Convert(converter = AttributesConverter.class)
  private Attributes attribute;

  @Column(nullable = true)
  @Convert(converter = SpecialityConverter.class)
  private Specialities speciality;

  @Column(nullable = true)
  @Convert(converter = TypeConverter.class)
  private Type type;

  @Column(nullable = true)
  private String faction;

  @Column(nullable = true)
  private Double version = 1.0;

}

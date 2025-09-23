package com.jplopez.zzz.entities;

import com.jplopez.zzz.entities.converter.SpecialityConverter;
import com.jplopez.zzz.entities.enums.Specialties;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class WEngine extends RepresentationModel<WEngine> {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Exclude
  private String id;

  @Column(nullable = false, unique = true)
  private String name; // Using name as ID for simplicity, similar to Agent

  @Column(nullable = false)
  @Convert(converter = SpecialityConverter.class)
  private Specialties speciality;

  @Column(nullable = true)
  private int baseValue;

  @Column(nullable = true)
  private String subStatName;

  @Column(nullable = true)
  private int subStatValue;

  @Column(nullable = true)
  private String skillName;

  @Column(nullable = true)
  private String skillDescription;

  // Constructor with fields
  public WEngine(String id, String name, Specialties speciality) {
    this.id = id;
    this.name = name;
    this.speciality = speciality;
  }

}

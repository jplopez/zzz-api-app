package com.jplopez.zzz.entities;

import org.springframework.hateoas.RepresentationModel;

import com.jplopez.zzz.entities.converter.RarityConverter;
import com.jplopez.zzz.entities.enums.Rarity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class Bangboo extends RepresentationModel<Bangboo>
 {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Exclude
  private String id;

  @Column(nullable = false, unique = true)
  private String bangbooId;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = true)
  @Convert(converter = RarityConverter.class)
  private Rarity rarity;

  @Column(nullable = true)
  private String faction;

}

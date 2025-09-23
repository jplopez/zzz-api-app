package com.jplopez.zzz.entities;

import org.springframework.hateoas.RepresentationModel;

import com.jplopez.zzz.entities.converter.StatTypeConverter;
import com.jplopez.zzz.entities.enums.StatTypes;

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
public class DiscDrive extends RepresentationModel<DiscDrive> {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Exclude
  private String id;

  @Column(nullable = false, unique = true)
  private String discDriveId;

  @Column(nullable = true)
  private int position;

  @Column(nullable = true)
  @Convert(converter = StatTypeConverter.class)
  private StatTypes type;

  @Column(nullable = true)
  private float baseValue;

  @Column(nullable = true)
  private String discDriveSetId;
}

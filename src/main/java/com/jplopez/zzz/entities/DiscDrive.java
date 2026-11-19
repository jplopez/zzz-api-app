package com.jplopez.zzz.entities;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

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
@Relation(collectionRelation = "discDriveList")
public class DiscDrive extends RepresentationModel<DiscDrive> {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Exclude
  private String id;

  @Column(nullable = false, unique = true)
  private String discDriveId;

  @Column(nullable = false)
  private int position;

  @Convert(converter = StatTypeConverter.class)
  private StatTypes type;

  private Float baseValue;

  /** The database ID of the containing set, if assigned. */
  private String discDriveSetId;
}

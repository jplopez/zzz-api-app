package com.jplopez.zzz.entities;

import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.Column;
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
public class DiscDriveSet extends RepresentationModel<DiscDriveSet> {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Exclude
  private String id;

  @Column(nullable = false, unique = true)
  private String discDriveSetId;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = true)
  private String description;

  @Column(nullable = true)
  private String twoPieceSkillDescription;

  @Column(nullable = true)
  private String fourPieceSkillDescription;

  // Manual getter for ID to ensure it's available
  public String getId() {
    return id;
  }

  // Manual setter for ID 
  public void setId(String id) {
    this.id = id;
  }

}

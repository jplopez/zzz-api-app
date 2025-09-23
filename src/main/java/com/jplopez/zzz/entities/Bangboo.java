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

/**
 * Bangboo Entity.
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
public class Bangboo extends RepresentationModel<Bangboo> {

  public Bangboo(String id, String bangbooId, String name) {
    this(id, bangbooId, name, 1.0f);
  }

  public Bangboo(String id, String bangbooId, String name, Float version) {
    super();
    this.id = id;
    this.bangbooId = bangbooId;
    this.name = name;
    this.version = version;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Exclude
  private String id;

  @Column(nullable = false, unique = true)
  private String bangbooId;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = true)
  private Float version = 1.0f;

  @Column(nullable = true)
  @Convert(converter = RarityConverter.class)
  private Rarity rarity;

  @Column(nullable = true)
  private String faction;

  /**
   * Checks if the Model was released in a specific version
   * @param version
   * @return
   */
  public boolean isVersion(float version) {
    return this.version.equals(version);
  }

  /**
   * Whether this entity was released after a specific version
   * @param version
   * @return
   */
  public boolean afterVersion(float version) {
    return this.version > version;
  }

  /**
   * Whether this entity was released after or at a specific version
   * @param version
   * @return
   */
  public boolean afterOrAtVersion(float version) {
    return this.version >= version;
  }

  /**
   * Whether this entity was released before a specific version
   * @param version
   * @return
   */
  public boolean beforeVersion(float version) {
    return this.version < version;
  }

  /**
   * Whether this entity was released before or at a specific version
   * @param version
   * @return
   */
  public boolean beforeOrAtVersion(float version) {
    return this.version <= version;
  }

  /**
   * Convenient method to check if an entity was present in a specific version.
   * @param version
   * @return
   */
  public boolean wasInVersion(float version) {
    return beforeOrAtVersion(version);
  }
}

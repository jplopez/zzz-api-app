package com.jplopez.zzz.entities;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.Nullable;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;

public abstract class ZZZEntityModel<T extends ZZZEntityModel<T>> extends RepresentationModel<T> {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Exclude
  private String id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = true)
  private Double version = 1.0;

  public String getEntityName() {
    return getClass().getSimpleName();
  }

  ZZZEntityModel() {
    super();
  }

  ZZZEntityModel(String id, String name) {
    this();
    this.id = id;
    this.name = name;
  }

  ZZZEntityModel(String id, String name, Double version) {
    this(id, name);
    this.version = version;
  }

  ZZZEntityModel<T> withId(String id) {
    this.id = id;
    return this;
  }

  ZZZEntityModel<T> withName(String name) {
    this.name = name;
    return this;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Double getVersion() {
    return version;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    ZZZEntityModel other = (ZZZEntityModel) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}

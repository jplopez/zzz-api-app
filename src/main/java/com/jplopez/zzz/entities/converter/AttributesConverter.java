package com.jplopez.zzz.entities.converter;

import java.util.stream.Stream;

import com.jplopez.zzz.entities.enums.Attributes;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * Converter for the cast an Enum <code>Attributes</code> to a String.
 * 
 * This class is used to successfully retrieve the 'attribute' stat of Agents and cast it
 * to  an instance of the <code>Attributes</code> Enums. 
 * 
 * @since 1.0
 */
@Slf4j
@Converter(autoApply = true)
public class AttributesConverter implements AttributeConverter<Attributes, String> {

  @Override
  public String convertToDatabaseColumn(Attributes attribute) {
    if (attribute == null) return "";
    return attribute.name();
  }

  @Override
  public Attributes convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) return null;

    return Stream.of(Attributes.values())
    .filter(c -> c.name().equals(dbData.toUpperCase()))
    .findFirst()
    .orElseThrow(IllegalArgumentException::new);
  }
}

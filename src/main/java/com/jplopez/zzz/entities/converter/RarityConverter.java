package com.jplopez.zzz.entities.converter;

import java.util.stream.Stream;

import com.jplopez.zzz.entities.enums.Rarity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * Converter for the cast an Enum <code>Rarity</code> to a String.
 * 
 * This class is used to successfully retrieve the 'rarity' stat of Agents and cast it
 * to  an instance of the <code>Rarity</code> Enums. 
 * 
 * @since 1.0
 */
@Slf4j
@Converter(autoApply = true)
public class RarityConverter implements AttributeConverter<Rarity, String> {

  @Override
  public String convertToDatabaseColumn(Rarity rarity) {
    if (rarity == null) return "";
    return rarity.name();
  }

  @Override
  public Rarity convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) return null;

    return Stream.of(Rarity.values())
    .filter(c -> c.name().equalsIgnoreCase(dbData))
    .findFirst()
    .orElseThrow(IllegalArgumentException::new);
  }

}
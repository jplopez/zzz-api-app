package com.jplopez.zzz.entities.converter;

import java.util.stream.Stream;

import com.jplopez.zzz.entities.enums.Specialties;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * Converter for the cast an Enum <code>Speciality</code> to a String.
 * 
 * This class is used to successfully retrieve the 'speciality' stat of Agents and cast it
 * to  an instance of the <code>Speciality</code> Enums. 
 * 
 * @since 1.0
 */
@Slf4j
@Converter(autoApply = true)
public class SpecialityConverter implements AttributeConverter<Specialties, String> {

  @Override
  public String convertToDatabaseColumn(Specialties spec) {
    if (spec == null) return "";
    return spec.name();
  }

  @Override
  public Specialties convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) return null;

    return Stream.of(Specialties.values())
    .filter(c -> c.name().equalsIgnoreCase(dbData))
    .findFirst()
    .orElseThrow(IllegalArgumentException::new);
  }
}

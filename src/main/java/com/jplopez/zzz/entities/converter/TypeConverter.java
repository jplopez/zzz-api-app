package com.jplopez.zzz.entities.converter;

import java.util.stream.Stream;

import com.jplopez.zzz.entities.enums.Type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter(autoApply = true)
public class TypeConverter implements AttributeConverter<Type, String> {

  @Override
  public String convertToDatabaseColumn(Type type) {
    if (type == null) return null;
    return type.name();
  }

  @Override
  public Type convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) return null;

    return Stream.of(Type.values())
    .filter(c -> c.name().equalsIgnoreCase(dbData))
    .findFirst()
    .orElseThrow(IllegalArgumentException::new);

  }
}

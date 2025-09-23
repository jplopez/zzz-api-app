package com.jplopez.zzz.entities.converter;

import java.util.stream.Stream;

import com.jplopez.zzz.entities.enums.StatTypes;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter(autoApply = true)
public class StatTypeConverter implements AttributeConverter<StatTypes, String> {

  @Override
  public String convertToDatabaseColumn(StatTypes arg0) {
    if(arg0 == null) return "";
    return arg0.name();
  }

  @Override
  public StatTypes convertToEntityAttribute(String dbData) {
    if(dbData == null || dbData.isEmpty()) return null;

    return Stream.of(StatTypes.values())
      .filter(c -> c.name().equals(dbData.toUpperCase()))
      .findFirst()
      .orElseThrow(IllegalArgumentException::new);
  }

}

package com.jplopez.zzz.common;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EnumUtils {

  public static <T extends Enum<T>> String[] getStringValues(Class<T> enumClass) {
    return getStringValuesWithStringExtractor(enumClass, Enum::name);
  }

  public static <T extends Enum<T>> String[] getStringValuesWithStringExtractor(
      Class<T> enumClass,
      Function<? super T, String> extractor) {
    return Stream.of(enumClass.getEnumConstants()).map(extractor).toArray(String[]::new);
  }

}
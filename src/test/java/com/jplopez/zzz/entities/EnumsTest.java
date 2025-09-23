package com.jplopez.zzz.entities;

import org.junit.jupiter.api.Test;

import com.jplopez.zzz.entities.enums.Attributes;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialties;
import com.jplopez.zzz.entities.enums.Type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;

/**
 * Test for Enums: Attributes, Rarity, Specialities, Type
 * 
 * @since 1.0
 */
public class EnumsTest {

  Attributes[] attributes = Attributes.values();
  Rarity[] values = Rarity.values();
  Specialties[] specs = Specialties.values();
  Type[] type = Type.values();

  @Test
  void testAttributesToString() {
    Arrays.stream(attributes).forEach(en -> {
      String str = en.toString();
      assertThat(str, oneOf("ELECTRIC", "ETHER", "FIRE", "ICE", "PHYSICAL"));
    });
  }

  @Test
  void testAttributesValues() {
    Arrays.stream(attributes).forEach(en -> {
      assertThat(en, oneOf(
          Attributes.ELECTRIC,
          Attributes.ETHER,
          Attributes.FIRE,
          Attributes.ICE,
          Attributes.PHYSICAL));
    });
  }

  @Test
  void testRarityToString() {
    Arrays.stream(values).forEach(en -> {
      String str = en.toString();
      assertThat(str, oneOf("A","B","S"));
    });
  }

  @Test
  void testRarityValues() {
    Arrays.stream(values).forEach(en -> {
      assertThat(en, oneOf(
          Rarity.A,
          Rarity.B,
          Rarity.S));
    });
  }

  @Test
  void testSpecialitiesToString() {
    Arrays.stream(specs).forEach(en -> {
      String str = en.toString();
      assertThat(str, oneOf("ANOMALY", "ATTACK", "SHIELD", "STUN", "SUPPORT"));
    });
  }

  @Test
  void testSpecialitiesValues() {
    Arrays.stream(specs).forEach(en -> {
      assertThat(en, oneOf(
          Specialties.ANOMALY,
          Specialties.ATTACK,
          Specialties.SHIELD,
          Specialties.STUN,
          Specialties.SUPPORT));
    });
  }

  @Test
  void testTypeToString() {
    Arrays.stream(type).forEach(en -> {
      String str = en.toString();
      assertThat(str, oneOf("SLASH", "STRIKE", "PIERCE"));
    });
  }

  @Test
  void testTypeValues() {
    Arrays.stream(type).forEach(en -> {
      assertThat(en, oneOf(
          Type.SLASH,
          Type.STRIKE,
          Type.PIERCE));
    });
  }


}

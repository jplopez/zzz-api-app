package com.jplopez.zzz.mocks;

import java.util.ArrayList;
import java.util.List;

import com.jplopez.zzz.entities.Bangboo;
import com.jplopez.zzz.entities.enums.Rarity;

/**
 * Mock class for creating test Bangboo instances.
 * 
 * @since 1.0
 */
public class BangbooMock {

  public static Bangboo amillionMockBangboo() {
    Bangboo b = new Bangboo("1", "1", "Amillion");
    b.setRarity(Rarity.S);
    b.setFaction("Victoria Housekeeping");
    b.setVersion(1.0f);
    return b;
  }
  
  public static Bangboo butlerMockBangboo() {
    Bangboo b = new Bangboo("2", "2", "Butler");
    b.setRarity(Rarity.A);
    b.setFaction("Victoria Housekeeping");
    b.setVersion(1.0f);
    return b;
  }
  
  public static Bangboo pengbooMockBangboo() {
    Bangboo b = new Bangboo("3", "3", "Pengboo");
    b.setRarity(Rarity.A);
    b.setFaction("Belobog Heavy Industries");
    b.setVersion(1.0f);
    return b;
  }
  
  public static Bangboo bagbooMockBangboo() {
    Bangboo b = new Bangboo("4", "4", "Bagboo");
    b.setRarity(Rarity.A);
    b.setFaction("Cunning Hares");
    b.setVersion(1.0f);
    return b;
  }
  
  public static Bangboo electrobooMockBangboo() {
    Bangboo b = new Bangboo("5", "5", "Electroboo");
    b.setRarity(Rarity.B);
    b.setFaction(null); // Nullable faction
    b.setVersion(1.0f);
    return b;
  }
  
  public static Bangboo nullRarityMockBangboo() {
    Bangboo b = new Bangboo("6", "6", "TestBoo");
    b.setRarity(null); // Nullable rarity for testing
    b.setFaction("Test Faction");
    b.setVersion(1.0f);
    return b;
  }
  
  public static List<Bangboo> bangbooList(int count) {
    ArrayList<Bangboo> mockList = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      mockList.add(random());
    }
    return mockList;
  }
  
  static Bangboo random() {
    Bangboo[] bangboos = { 
      amillionMockBangboo(), 
      butlerMockBangboo(), 
      pengbooMockBangboo(), 
      bagbooMockBangboo(), 
      electrobooMockBangboo() 
    };
    return bangboos[(int) Math.floor(Math.random() * bangboos.length)];
  }

}
package com.jplopez.zzz.mocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jplopez.zzz.entities.Wengine;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialities;

/**
 * @since 1.0
 */
public class WengineMock {

  public static Wengine streetSuperstarMockWengine() {
    Wengine w = new Wengine("1", "1", "Street Superstar");
    w.setDescription("A versatile W-Engine that boosts attack damage and skill performance.");
    w.setRarity(Rarity.S);
    w.setBaseAttack(594);
    w.setSpecialty(Specialities.ATTACK);
    w.setSkillDescriptions(Arrays.asList(
        "Increases ATK by 25%.",
        "When the equipping character uses a Basic Attack, Dodge Counter, or Assist Attack, their DMG increases by 20% for 6s."
    ));
    return w;
  }
  
  public static Wengine cannonRoamerMockWengine() {
    Wengine w = new Wengine("2", "2", "Cannon Roamer");
    w.setDescription("A W-Engine designed for Anomaly agents, enhancing their anomaly buildup capabilities.");
    w.setRarity(Rarity.S);
    w.setBaseAttack(624);
    w.setSpecialty(Specialities.ANOMALY);
    w.setSkillDescriptions(Arrays.asList(
        "Increases ATK by 20%.",
        "When the equipped character inflicts an Attribute Anomaly or triggers a Disorder, their ATK increases by 25% for 8s."
    ));
    return w;
  }
  
  public static Wengine steamOvenMockWengine() {
    Wengine w = new Wengine("3", "3", "Steam Oven");
    w.setDescription("A support-oriented W-Engine that enhances team capabilities.");
    w.setRarity(Rarity.A);
    w.setBaseAttack(463);
    w.setSpecialty(Specialities.SUPPORT);
    w.setSkillDescriptions(Arrays.asList(
        "Increases ATK by 15%.",
        "When the equipped character uses an EX Special Attack, all squad members' DMG increases by 10% for 10s."
    ));
    return w;
  }
  
  public static List<Wengine> wengineList(int count) {
    ArrayList<Wengine> mockList = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      mockList.add(random());
    }
    return mockList;
  }
  
  static Wengine random() {
    Wengine[] wengines = { streetSuperstarMockWengine(), cannonRoamerMockWengine(), steamOvenMockWengine() };
    return wengines[(int) Math.floor(Math.random() * wengines.length)];
  }

}
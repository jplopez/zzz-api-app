package com.jplopez.zzz.mocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jplopez.zzz.entities.Agent;
import com.jplopez.zzz.entities.enums.Attributes;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialities;
import com.jplopez.zzz.entities.enums.Type;

/**
 * @since 1.0
 */
public class AgentMock {

  public static Agent lycaonMockAgent() {
    Agent a = new Agent("1", "1", "Lycaon");
    a.setRarity(Rarity.S);
    a.setAttribute(Attributes.ICE);
    a.setSpeciality(Specialities.STUN);
    a.setType(Type.STRIKE);
    a.setFaction("Victoria Housekeeping");
    a.setVersion(1.0);
    return a;
  }
  
  public static Agent zhuyuanMockAgent() {
    Agent a = new Agent("2", "2", "Zhu Yuan");
    a.setRarity(Rarity.S);
    a.setAttribute(Attributes.ETHER);
    a.setSpeciality(Specialities.ATTACK);
    a.setType(Type.PIERCE);
    a.setFaction("N.E.P.S");
    a.setVersion(1.1);
    return a;
  }
  
  public static Agent burniceMockAgent() {
    Agent a = new Agent("3", "3", "Burnice");
    a.setRarity(Rarity.S);
    a.setAttribute(Attributes.FIRE);
    a.setSpeciality(Specialities.ANOMALY);
    a.setType(Type.PIERCE);
    a.setFaction("Sons of Calydon");
    a.setVersion(1.2);
    return a;
  }
  
  public static List<Agent> agentList(int count) {
    ArrayList<Agent> mockList = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      mockList.add(random());
    }
    return mockList;
  }
  
  static Agent random() {
    Agent[] agents = { lycaonMockAgent(), zhuyuanMockAgent(), burniceMockAgent() };
    return agents[(int) Math.floor(Math.random() * agents.length)];
  }

  static Object createOneSkill(String name) {
    Map<String, String> skill = new HashMap<>();
    skill.put("name",name);
    skill.put("dmg_base", Math.random()*50.0+"");
    skill.put("dmg_growth", Math.random()*5.0+"");
    skill.put("stun_base", Math.random()*50.0+"");
    skill.put("stun_growth", Math.random()*5.0+"");
    return skill;
  }

  static List<Object> createSkills() {
    List<Object> skills = new ArrayList<>();
    skills.add(createOneSkill("normal"));
    skills.add(createOneSkill("special"));
    skills.add(createOneSkill("special_ex"));
    skills.add(createOneSkill("dodge"));
    skills.add(createOneSkill("assist"));
    skills.add(createOneSkill("ultimate"));
    return skills;
  }

}

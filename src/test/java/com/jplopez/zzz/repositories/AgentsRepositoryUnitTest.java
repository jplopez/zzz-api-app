package com.jplopez.zzz.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import static org.apache.commons.lang3.RandomStringUtils.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jplopez.zzz.app.ZzzApiAppApplication;
import com.jplopez.zzz.entities.Agent;
import com.jplopez.zzz.entities.enums.Attributes;
import com.jplopez.zzz.entities.enums.Rarity;
import com.jplopez.zzz.entities.enums.Specialities;
import com.jplopez.zzz.entities.enums.Type;

@SpringBootTest(classes = ZzzApiAppApplication.class)
class AgentsRepositoryUnitTest {

  @Autowired
  private AgentsRepository repo;

  @Test
  void findAllTest() {
    Iterable<Agent> agents = repo.findAll();
    agents.forEach(agent -> weakAssertAgent(agent));
  }

  @Test
  void findById_thenOK() {
    Optional<Agent> optional = repo.findById("16");
    assertFalse(optional.isEmpty());
    Agent obtained = optional.get();
    assertEquals("Zhu Yuan", obtained.getFullName());
    assertEquals(Rarity.S, obtained.getRarity());
    assertEquals(Attributes.ETHER, obtained.getAttribute());
    assertEquals(Specialities.ATTACK, obtained.getSpeciality());
    assertNotNull(obtained.getAgentId());
    assertEquals(Type.PIERCE,obtained.getType());
  }

  @Test
  void findById_thenNotFound() {
    Optional<Agent> optional = repo.findById(randomNumeric(4));
    assertFalse(optional.isPresent());
  }

  @Test
  void findByName_thenOK() {
    Iterable<Agent> agents = repo.findByNameIgnoreCase("Lycaon");
    assertThat(agents, hasItem( hasProperty("name", is("lycaon"))));
  }

  @Test
  void findByFullNameContaining_thenReturnOne() {
    Iterable<Agent> agents = repo.findByFullNameContaining("Lycaon");
    assertThat(agents, hasItem( hasProperty("fullName", is("Von Lycaon"))));
  }

  @Test
  void findByFullNameContaining_thenReturnMany() {
    Iterable<Agent> agents = repo.findByFullNameContaining("Demara");
    assertThat(agents, allOf(
      hasItem( hasProperty("fullName", is("Anby Demara"))),
      hasItem( hasProperty("fullName", is("Nicole Demara")))
    ));
  }

  @Test
  void findByFullNameContaining_thenReturnZero() {
    Iterable<Agent> agents = repo.findByFullNameContaining("Starlight Knight");
    assertThat(agents, is(emptyIterable()));
  }

  @Test
  void findByRarityA_thenReturnMany() {
    Iterable<Agent> agents = repo.findByRarity(Rarity.A);
    assertThat(agents, everyItem(
        hasProperty("rarity", is(Rarity.A))));
  }
  
  @Test
  void findByRarityB_thenReturnZero() {
    Iterable<Agent> agents = repo.findByRarity(Rarity.B); //for agents there are only S and A rarity
    assertThat(agents, is(emptyIterable()));
  }

  @Test
  void findByAttribute_thenOK() {
    Iterable<Agent> agents = repo.findByAttribute(Attributes.FIRE);
    assertThat(agents, everyItem(
        hasProperty("attribute", is(Attributes.FIRE))));
  }

  @Test
  void findByManyAttributes_thenReturnMany() {
    List<Attributes> attrs = Arrays.asList(Attributes.ELECTRIC, Attributes.ETHER, Attributes.ICE);

    Iterable<Agent> agents = repo.findByAttributeIn(attrs);
    
    assertThat(agents, everyItem(
      hasProperty("attribute", in(attrs))));
  }

  @Test
  void findByOneSpecialty_thenReturnMany() {
    Iterable<Agent> agents = repo.findBySpeciality(Specialities.ATTACK);
    assertThat(agents, everyItem(
        hasProperty("speciality", is(Specialities.ATTACK))));
  }

  @Test
  void findByManySpecialty_thenReturnMany() {
    List<Specialities> specs = Arrays.asList(Specialities.ANOMALY, Specialities.SHIELD, Specialities.SUPPORT);

    Iterable<Agent> agents = repo.findBySpecialityIn(specs);
    assertThat(agents, everyItem(
      hasProperty("speciality", in(specs))));
  }

  @Test
  void findByOneType_thenReturnMany() {
    Iterable<Agent> agents = repo.findByType(Type.PIERCE);
    assertThat(agents, everyItem(
        hasProperty("type", is(Type.PIERCE))));
  }

  @Test
  void findByManyTypes_thenReturnMany() {
    List<Type> types = Arrays.asList(Type.PIERCE, Type.SLASH);

    Iterable<Agent> agents = repo.findByTypeIn(types);
    assertThat(agents, everyItem(
      hasProperty("type", in(types))));
  }

  @Test
  void findByFactionContaining_thenReturnMany() {
    Iterable<Agent> agents = repo.findByFactionContaining("Special");
    assertThat(agents, allOf(
      hasItem( hasProperty("faction", is("Criminal Investigation Special Response Team"))),
      hasItem( hasProperty("faction", is("Hollow Special Operations Section 6")))
    ));

  }

  @Test
  void findByFactionContaining_thenReturnZero() {
    Iterable<Agent> agents = repo.findByFullNameContaining("Diamond Squad");
    assertThat(agents, is(emptyIterable()));
  }

  @Test
  void findByOneVersion_thenReturnMany() {
    Iterable<Agent> agents = repo.findByVersion(1.0d);
    assertThat(agents, everyItem(
        hasProperty("version", is(1.0d))));
  }

  @Test
  void findByVersionBetween_thenReturnMany() {
    Iterable<Agent> agents = repo.findByVersionBetween(1.0d, 1.2d);
    Double[] expectedVersions = {1.0d,1.1d,1.2d};
    assertThat(agents, everyItem(
        hasProperty("version", in(expectedVersions))));
  }

  @Test
  void findByVersionIn_thenReturnMany() {
    Double[] expectedVersions = {1.0d,1.2d};
    Iterable<Agent> agents = repo.findByVersionIn(List.of(expectedVersions));
    assertThat(agents, everyItem(
        hasProperty("version", in(expectedVersions))));
  }


  @Test
  void searchByRarityAndType_thenReturnMany() {
    Rarity rarity = Rarity.S;
    Type type = Type.PIERCE;

    // rarity and attackStyle
    Iterable<Agent> agents = repo.findByNameOrRarityOrAttributeOrSpecialityOrTypeOrFactionOrVersion(
      null, rarity, null, null, type, null, null);
    
    agents.forEach(agent -> {
    assertThat(agent, 
      anyOf(
          hasProperty("type", equalTo(type)), 
          hasProperty("rarity", equalTo(rarity)) ));
    });
  }

  @Disabled("Pending GraphQL integration")
  @Test
  void searchBySpeciality_thenReturnOne() {
    Specialities spec = Specialities.ATTACK;
    // rarity and attackStyle
    Iterable<Agent> agents = repo.findByNameOrRarityOrAttributeOrSpecialityOrTypeOrFactionOrVersion(
      null, null, null, spec, null, null, null);
     
      agents.forEach(agent -> assertEquals(spec, agent.getSpeciality()));
  }

  @Disabled("Pending GraphQL integration")
  @Test
  void searchByNonExistentFaction_thenReturnZero() {
    String nonFaction = "Diamond Division";
    Iterable<Agent> agents = repo.findByNameOrRarityOrAttributeOrSpecialityOrTypeOrFactionOrVersion(
      null, null, null, null, null, nonFaction, null);
    assertThat(agents, is(emptyIterable()));
  }

  private void weakAssertAgent(Agent agent) {
    assertNotNull(agent.getId());
    assertNotNull(agent.getAgentId());
    assertNotNull(agent.getName());
  }

}

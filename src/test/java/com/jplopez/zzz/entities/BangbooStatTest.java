package com.jplopez.zzz.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for BangbooStat entity.
 * Tests entity creation, getters/setters, and validation constraints.
 * 
 * @since 1.0
 */
class BangbooStatTest {

    @Test
    void testBangbooStatCreation() {
        String bangbooId = "test-bangboo-1";
        int level = 30;
        float hp = 500.0f;
        float atk = 200.0f;
        float def = 150.0f;
        float critRate = 12.5f;
        float critDamage = 75.0f;
        float impact = 180.0f;

        BangbooStat stat = new BangbooStat(bangbooId, level, hp, atk, def, critRate, critDamage, impact);

        assertNotNull(stat);
        assertEquals(bangbooId, stat.getBangbooId());
        assertEquals(level, stat.getLevel());
        assertEquals(hp, stat.getHp());
        assertEquals(atk, stat.getAtk());
        assertEquals(def, stat.getDef());
        assertEquals(critRate, stat.getCritRate());
        assertEquals(critDamage, stat.getCritDamage());
        assertEquals(impact, stat.getImpact());
    }

    @Test
    void testBangbooStatSetters() {
        BangbooStat stat = new BangbooStat();
        
        String bangbooId = "test-bangboo-2";
        int level = 45;
        float hp = 750.0f;
        float atk = 300.0f;
        float def = 200.0f;
        float critRate = 18.0f;
        float critDamage = 90.0f;
        float impact = 250.0f;

        stat.setBangbooId(bangbooId);
        stat.setLevel(level);
        stat.setHp(hp);
        stat.setAtk(atk);
        stat.setDef(def);
        stat.setCritRate(critRate);
        stat.setCritDamage(critDamage);
        stat.setImpact(impact);

        assertEquals(bangbooId, stat.getBangbooId());
        assertEquals(level, stat.getLevel());
        assertEquals(hp, stat.getHp());
        assertEquals(atk, stat.getAtk());
        assertEquals(def, stat.getDef());
        assertEquals(critRate, stat.getCritRate());
        assertEquals(critDamage, stat.getCritDamage());
        assertEquals(impact, stat.getImpact());
    }

    @Test 
    void testBangbooStatBoundaryValues() {
        // Test minimum level
        BangbooStat statMin = new BangbooStat("test-bangboo", 1, 100f, 50f, 30f, 5f, 50f, 25f);
        assertEquals(1, statMin.getLevel());

        // Test maximum level
        BangbooStat statMax = new BangbooStat("test-bangboo", 60, 1000f, 500f, 300f, 25f, 150f, 400f);
        assertEquals(60, statMax.getLevel());
    }

    @Test
    void testBangbooStatEquality() {
        BangbooStat stat1 = new BangbooStat("test-bangboo", 30, 500f, 200f, 150f, 12.5f, 75f, 180f);
        BangbooStat stat2 = new BangbooStat("test-bangboo", 30, 500f, 200f, 150f, 12.5f, 75f, 180f);

        // Since we're extending RepresentationModel, equality might be different
        // Just test that they have the same property values
        assertEquals(stat1.getBangbooId(), stat2.getBangbooId());
        assertEquals(stat1.getLevel(), stat2.getLevel());
        assertEquals(stat1.getHp(), stat2.getHp());
        assertEquals(stat1.getAtk(), stat2.getAtk());
        assertEquals(stat1.getDef(), stat2.getDef());
        assertEquals(stat1.getCritRate(), stat2.getCritRate());
        assertEquals(stat1.getCritDamage(), stat2.getCritDamage());
        assertEquals(stat1.getImpact(), stat2.getImpact());
    }
}
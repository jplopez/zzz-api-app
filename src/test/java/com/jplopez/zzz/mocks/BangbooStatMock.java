package com.jplopez.zzz.mocks;

import java.util.ArrayList;
import java.util.List;

import com.jplopez.zzz.entities.BangbooStat;

/**
 * Mock data generator for BangbooStat entities
 * 
 * @since 1.0
 */
public class BangbooStatMock {

    /**
     * Creates a list of mock BangbooStat entities for a given bangboo ID across all levels (1-60)
     * @param bangbooId the bangboo ID to create stats for
     * @return list of BangbooStat entities for levels 1-60
     */
    public static List<BangbooStat> createMockStatsForBangboo(String bangbooId) {
        List<BangbooStat> stats = new ArrayList<>();
        
        for (int level = 1; level <= 60; level++) {
            // Simple linear progression for stats (this would normally be more complex)
            float baseHP = 100.0f;
            float baseAtk = 50.0f;
            float baseDef = 30.0f;
            float baseCritRate = 5.0f;
            float baseCritDamage = 50.0f;
            float baseImpact = 25.0f;
            
            float levelMultiplier = 1.0f + (level - 1) * 0.1f;
            
            BangbooStat stat = new BangbooStat(
                bangbooId,
                level,
                baseHP * levelMultiplier,
                baseAtk * levelMultiplier,
                baseDef * levelMultiplier,
                baseCritRate + (level - 1) * 0.2f,
                baseCritDamage + (level - 1) * 0.5f,
                baseImpact * levelMultiplier
            );
            
            stats.add(stat);
        }
        
        return stats;
    }

    /**
     * Creates a single mock BangbooStat entity for a given bangboo ID and level
     * @param bangbooId the bangboo ID
     * @param level the level (1-60)
     * @return BangbooStat entity
     */
    public static BangbooStat createMockStatForLevel(String bangbooId, int level) {
        float baseHP = 100.0f;
        float baseAtk = 50.0f;
        float baseDef = 30.0f;
        float baseCritRate = 5.0f;
        float baseCritDamage = 50.0f;
        float baseImpact = 25.0f;
        
        float levelMultiplier = 1.0f + (level - 1) * 0.1f;
        
        return new BangbooStat(
            bangbooId,
            level,
            baseHP * levelMultiplier,
            baseAtk * levelMultiplier,
            baseDef * levelMultiplier,
            baseCritRate + (level - 1) * 0.2f,
            baseCritDamage + (level - 1) * 0.5f,
            baseImpact * levelMultiplier
        );
    }
}
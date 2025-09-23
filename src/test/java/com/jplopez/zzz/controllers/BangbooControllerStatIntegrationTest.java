package com.jplopez.zzz.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;

import com.jplopez.zzz.common.exceptions.ZZZEntityNotFoundException;
import com.jplopez.zzz.entities.Bangboo;
import com.jplopez.zzz.entities.BangbooStat;
import com.jplopez.zzz.repositories.BangbooRepository;
import com.jplopez.zzz.repositories.BangbooStatRepository;
import com.jplopez.zzz.mocks.BangbooMock;
import com.jplopez.zzz.mocks.BangbooStatMock;

/**
 * Integration tests for BangbooController stat endpoints.
 * Tests the new /bangboos/{id}/stats and /bangboos/{id}/stats/{level} endpoints.
 * 
 * @since 1.0
 */
@WebMvcTest(BangbooController.class)
class BangbooControllerStatIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BangbooRepository bangbooRepository;

    @MockBean
    private BangbooStatRepository bangbooStatRepository;

    private Bangboo testBangboo;
    private List<BangbooStat> testStats;

    @BeforeEach
    void setUp() {
        testBangboo = BangbooMock.amillionMockBangboo();
        testStats = BangbooStatMock.createMockStatsForBangboo(testBangboo.getId());
    }

    @Test
    void testGetBangbooStats() throws Exception {
        // Given
        given(bangbooRepository.findById(testBangboo.getId()))
            .willReturn(Optional.of(testBangboo));
        given(bangbooStatRepository.findByBangbooIdOrderByLevelAsc(testBangboo.getId()))
            .willReturn(testStats.subList(0, 5)); // Return first 5 levels

        // When & Then
        mockMvc.perform(get("/bangboos/" + testBangboo.getId() + "/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].bangbooId").value(testBangboo.getId()))
                .andExpect(jsonPath("$[0].level").value(1))
                .andExpect(jsonPath("$[4].level").value(5));
    }

    @Test
    void testGetBangbooStats_BangbooNotFound() throws Exception {
        // Given
        given(bangbooRepository.findById("non-existent"))
            .willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/bangboos/non-existent/stats"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetBangbooStatByLevel() throws Exception {
        // Given
        int testLevel = 30;
        BangbooStat testStat = BangbooStatMock.createMockStatForLevel(testBangboo.getId(), testLevel);
        
        given(bangbooRepository.findById(testBangboo.getId()))
            .willReturn(Optional.of(testBangboo));
        given(bangbooStatRepository.findByBangbooIdAndLevel(testBangboo.getId(), testLevel))
            .willReturn(Optional.of(testStat));

        // When & Then
        mockMvc.perform(get("/bangboos/" + testBangboo.getId() + "/stats/" + testLevel))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bangbooId").value(testBangboo.getId()))
                .andExpect(jsonPath("$.level").value(testLevel))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.allStats").exists())
                .andExpect(jsonPath("$._links.prev").exists()) // Level 30 should have prev
                .andExpect(jsonPath("$._links.next").exists()); // Level 30 should have next
    }

    @Test
    void testGetBangbooStatByLevel_Level1() throws Exception {
        // Given
        int testLevel = 1;
        BangbooStat testStat = BangbooStatMock.createMockStatForLevel(testBangboo.getId(), testLevel);
        
        given(bangbooRepository.findById(testBangboo.getId()))
            .willReturn(Optional.of(testBangboo));
        given(bangbooStatRepository.findByBangbooIdAndLevel(testBangboo.getId(), testLevel))
            .willReturn(Optional.of(testStat));

        // When & Then
        mockMvc.perform(get("/bangboos/" + testBangboo.getId() + "/stats/" + testLevel))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value(1))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.allStats").exists())
                .andExpect(jsonPath("$._links.prev").doesNotExist()) // Level 1 should not have prev
                .andExpect(jsonPath("$._links.next").exists()); // Level 1 should have next
    }

    @Test
    void testGetBangbooStatByLevel_Level60() throws Exception {
        // Given
        int testLevel = 60;
        BangbooStat testStat = BangbooStatMock.createMockStatForLevel(testBangboo.getId(), testLevel);
        
        given(bangbooRepository.findById(testBangboo.getId()))
            .willReturn(Optional.of(testBangboo));
        given(bangbooStatRepository.findByBangbooIdAndLevel(testBangboo.getId(), testLevel))
            .willReturn(Optional.of(testStat));

        // When & Then
        mockMvc.perform(get("/bangboos/" + testBangboo.getId() + "/stats/" + testLevel))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value(60))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.allStats").exists())
                .andExpect(jsonPath("$._links.prev").exists()) // Level 60 should have prev
                .andExpect(jsonPath("$._links.next").doesNotExist()); // Level 60 should not have next
    }

    @Test
    void testGetBangbooStatByLevel_BangbooNotFound() throws Exception {
        // Given
        given(bangbooRepository.findById("non-existent"))
            .willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/bangboos/non-existent/stats/30"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetBangbooStatByLevel_StatNotFound() throws Exception {
        // Given
        given(bangbooRepository.findById(testBangboo.getId()))
            .willReturn(Optional.of(testBangboo));
        given(bangbooStatRepository.findByBangbooIdAndLevel(testBangboo.getId(), 30))
            .willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/bangboos/" + testBangboo.getId() + "/stats/30"))
                .andExpect(status().isNotFound());
    }
}
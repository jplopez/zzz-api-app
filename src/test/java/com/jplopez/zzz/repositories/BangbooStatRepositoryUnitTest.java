package com.jplopez.zzz.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.jplopez.zzz.app.ZzzApiAppApplication;
import com.jplopez.zzz.entities.BangbooStat;
import com.jplopez.zzz.mocks.BangbooStatMock;

@SpringBootTest(classes = ZzzApiAppApplication.class)
@Transactional
class BangbooStatRepositoryUnitTest {

  @Autowired
  private BangbooStatRepository repository;

  @Test
  void findsStatsInLevelOrder() {
    String bangbooId = "2";
    repository.saveAllAndFlush(List.of(
        BangbooStatMock.createMockStatForLevel(bangbooId, 5),
        BangbooStatMock.createMockStatForLevel(bangbooId, 1),
        BangbooStatMock.createMockStatForLevel(bangbooId, 3)));

    assertThat(repository.findByBangbooIdOrderByLevelAsc(bangbooId))
        .extracting(BangbooStat::getLevel).containsExactly(1, 3, 5);
  }

  @Test
  void findsStatByBangbooAndLevel() {
    BangbooStat saved = repository.saveAndFlush(BangbooStatMock.createMockStatForLevel("3", 25));

    assertThat(repository.findByBangbooIdAndLevel("3", 25))
        .map(BangbooStat::getId).contains(saved.getId());
    assertThat(repository.findByBangbooIdAndLevel("2", 25)).isEmpty();
  }

  @Test
  void returnsEmptyResultsForUnknownBangboo() {
    assertThat(repository.findByBangbooIdAndLevel("missing", 1)).isEmpty();
    assertThat(repository.findByBangbooIdOrderByLevelAsc("missing")).isEmpty();
  }

  @Test
  void rejectsDuplicateStatsForSameBangbooAndLevel() {
    assertThatThrownBy(() -> repository.saveAndFlush(BangbooStatMock.createMockStatForLevel("1", 1)))
        .isInstanceOf(DataIntegrityViolationException.class);
  }
}

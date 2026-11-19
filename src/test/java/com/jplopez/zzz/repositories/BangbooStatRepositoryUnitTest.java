package com.jplopez.zzz.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.jplopez.zzz.app.ZzzApiAppApplication;
import com.jplopez.zzz.entities.BangbooStat;

@SpringBootTest(classes = ZzzApiAppApplication.class)
@Transactional
class BangbooStatRepositoryUnitTest {

  @Autowired
  private BangbooStatRepository repository;

  @Autowired
  private JdbcTemplate jdbc;

  @Test
  void findsStatsInLevelOrder() {
    assertThat(repository.findByBangbooIdOrderByLevelAsc("1"))
        .extracting(BangbooStat::getLevel).containsExactly(1, 2, 3);
  }

  @Test
  void findsStatByBangbooAndLevel() {
    assertThat(repository.findByBangbooIdAndLevel("1", 2)).hasValueSatisfying(stat -> {
      assertThat(stat.getId()).isEqualTo("amillion-2");
      assertThat(stat.getAtk()).isEqualTo(27f);
    });
    assertThat(repository.findByBangbooIdAndLevel("2", 2)).isEmpty();
  }

  @Test
  void returnsEmptyResultsForUnknownBangboo() {
    assertThat(repository.findByBangbooIdAndLevel("missing", 1)).isEmpty();
    assertThat(repository.findByBangbooIdOrderByLevelAsc("missing")).isEmpty();
  }

  @Test
  void rejectsDuplicateValuesForSameStatAndLevel() {
    assertThatThrownBy(() -> jdbc.update("insert into STAT_VALUE (STAT_ID, LEVEL, AMOUNT) values (?, ?, ?)",
        "amillion-atk", 1, 100))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void legacyRowsReflectChangesToAtomicValues() {
    jdbc.update("update STAT_VALUE set AMOUNT = ? where STAT_ID = ? and LEVEL = ?", 40, "amillion-atk", 1);
    assertThat(repository.findByBangbooIdAndLevel("1", 1)).hasValueSatisfying(stat ->
        assertThat(stat.getAtk()).isEqualTo(40f));
  }

  @Test
  void aPartialLevelIsNotExposedAsACompleteLegacyRow() {
    jdbc.update("insert into STAT_VALUE (STAT_ID, LEVEL, AMOUNT) values (?, ?, ?)", "amillion-atk", 4, 31);

    assertThat(repository.findByBangbooIdAndLevel("1", 4)).isEmpty();
  }
}

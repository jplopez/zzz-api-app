package com.jplopez.zzz.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jplopez.zzz.entities.DiscDriveSet;

/**
 * Repository to read data of Disc Drive Sets.
 * Repositories expose the operations available to do against a specific table.
 * 
 * @since 1.0
 */
@Repository
public interface DiscDriveSetRepository extends JpaRepository<DiscDriveSet, String> {

  List<DiscDriveSet> findByNameIgnoreCase(String name);

  List<DiscDriveSet> findByNameContaining(String input);

  List<DiscDriveSet> findByDiscDriveSetId(String discDriveSetId);

  List<DiscDriveSet> findByDescriptionContaining(String input);

  List<DiscDriveSet> findByTwoPieceSkillDescriptionContaining(String input);

  List<DiscDriveSet> findByFourPieceSkillDescriptionContaining(String input);

  List<DiscDriveSet> findByNameOrDescription(String name, String description);

}
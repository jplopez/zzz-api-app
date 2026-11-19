package com.jplopez.zzz.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jplopez.zzz.entities.DiscDriveSet;

public interface DiscDriveSetRepository extends JpaRepository<DiscDriveSet, String> {

  List<DiscDriveSet> findByNameIgnoreCase(String name);

  List<DiscDriveSet> findByNameContainingIgnoreCase(String name);

  List<DiscDriveSet> findByDescriptionContainingIgnoreCase(String description);

  List<DiscDriveSet> findByTwoPieceSkillDescriptionContainingIgnoreCase(String description);

  List<DiscDriveSet> findByFourPieceSkillDescriptionContainingIgnoreCase(String description);
}

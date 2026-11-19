package com.jplopez.zzz.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jplopez.zzz.entities.DiscDrive;
import com.jplopez.zzz.entities.enums.StatTypes;

public interface DiscDriveRepository extends JpaRepository<DiscDrive, String> {

  List<DiscDrive> findByPosition(int position);

  List<DiscDrive> findByType(StatTypes type);

  List<DiscDrive> findByBaseValue(float baseValue);

  List<DiscDrive> findByBaseValueBetween(float from, float to);

  List<DiscDrive> findByDiscDriveSetIdOrderByPositionAscIdAsc(String discDriveSetId);
}

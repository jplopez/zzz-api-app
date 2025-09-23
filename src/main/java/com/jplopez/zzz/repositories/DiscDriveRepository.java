package com.jplopez.zzz.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jplopez.zzz.entities.DiscDrive;
import com.jplopez.zzz.entities.enums.StatTypes;

/**
 * Repository to read data of Disc Drives.
 * Repositories expose the operations available to do against a specific table.
 * 
 * @since 1.0
 */
@Repository
public interface DiscDriveRepository extends JpaRepository<DiscDrive, String> {

  List<DiscDrive> findByDiscDriveId(String discDriveId);

  List<DiscDrive> findByPosition(int position);

  List<DiscDrive> findByType(StatTypes type);

  List<DiscDrive> findByTypeIn(Collection<StatTypes> types);

  List<DiscDrive> findByBaseValue(float baseValue);

  List<DiscDrive> findByBaseValueBetween(float from, float to);

  List<DiscDrive> findByDiscDriveSetId(String discDriveSetId);

  List<DiscDrive> findByPositionAndType(int position, StatTypes type);

  List<DiscDrive> findByDiscDriveSetIdAndPosition(String discDriveSetId, int position);

  List<DiscDrive> findByTypeOrBaseValue(StatTypes type, float baseValue);

}
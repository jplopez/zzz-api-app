package com.jplopez.zzz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jplopez.zzz.entities.Skill;


@Repository
public interface SkillsRepository extends JpaRepository<Skill, String> {

}

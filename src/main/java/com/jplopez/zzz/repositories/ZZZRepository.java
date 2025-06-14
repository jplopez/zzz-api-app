package com.jplopez.zzz.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZZZRepository<T> extends JpaRepository<T, String> {

  List<T> findByNameIgnoreCase(String name);

  List<T> findByVersion(Double version);

  List<T> findByVersionIn(Collection<Double> versions);

  List<T> findByVersionBetween(Double from, Double to);

}
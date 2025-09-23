package com.jplopez.zzz.entities.stats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jplopez.zzz.common.interfaces.LevelsDescriptor;
import com.jplopez.zzz.entities.enums.StatTypes;

import lombok.Data;
import lombok.ToString.Exclude;

@Data
public class Stat implements LevelsDescriptor {

  private String id;

  private StatTypes type;

  private String name;

  private float baseValue;

  private int maxLevel;

  @Exclude
  private List<Float> levelValues = new ArrayList<>();

  @Override
  public int maxLevel() {
    return maxLevel;
  }

  @Override
  public float getLevelValue(int level) {
    if(level < 0 || level > levelValues.size()) throw new ArrayIndexOutOfBoundsException("Stat doesnt have level " + level);
    return levelValues.get(level);
  }

  @Override
  public Iterator<Float> getIterator() {
    return levelValues.iterator();
  }

  @Override
  public float[] getLevelValues() 
  {
    float[] ret = new float[levelValues.size()];
    levelValues.forEach(f -> ret[ret.length]=f.floatValue());
    return ret;
  }



}

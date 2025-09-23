package com.jplopez.zzz.common.interfaces;

import java.util.Iterator;

public interface LevelsDescriptor {

  public int maxLevel();

  public float getLevelValue(int level);

  public Iterator<Float> getIterator();

  public float[] getLevelValues();

}

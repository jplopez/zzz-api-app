package com.jplopez.zzz.common.interfaces;

/**
 * Interface for ZZZEntities that were released on a specific version. 
 * This interface helps with common operations that are restricted by the version
 */
public interface Versionable {

  public float getReleasedVersion();

  public void setReleasedVersion(float versionValue);

  public boolean releasedBeforeVersion(float version);

  public boolean releasedBeforeOrAtVersion(float version);

  public boolean releasedAfterVersion(float version);

  public boolean releasedAfterOrAtVersion(float version);

  public boolean wasInVersion(float version);

}

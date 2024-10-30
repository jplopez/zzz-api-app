package com.jplopez.zzz.services;

import java.util.List;

import com.jplopez.zzz.common.exceptions.NotFoundException;
import com.jplopez.zzz.entities.Agent;

public interface AgentService {

  public List<Agent> getAll();

  public List<Agent> findByParameter(Agent params);

  public Agent findAgent(String id) throws NotFoundException;

}

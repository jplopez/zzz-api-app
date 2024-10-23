package com.jplopez.zzz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;

@RestController
@RequestMapping("/agents")
public class AgentsController {

  private final CosmosClient cosmosClient;
  private final CosmosDatabase database;
  private final CosmosContainer container;

  @Autowired
  public AgentsController() {
    this.cosmosClient = new CosmosClientBuilder()
        .endpoint("your_endpoint")
        .key("your_key")
        .buildClient();

    this.database = cosmosClient.getDatabase("your_database_name");
    this.container = database.getContainer("your_container_name");
  }

  @GetMapping("/data")
    public String getData(@RequestParam String name) {
        // Query Cosmos DB
        String query = "SELECT * FROM c where c.name = '" + name + "'";
        CosmosItemResponse<Object> response = container.queryItems(query, new CosmosQueryRequestOptions(), Object.class);
        
        return "Data from Cosmos DB";
    }
}

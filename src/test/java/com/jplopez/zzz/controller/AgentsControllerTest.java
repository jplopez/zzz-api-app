package com.jplopez.zzz.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosContainer;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AgentsControllerTest {

  @Mock
  private CosmosClient cosmosClient;

  @Mock
  private CosmosDatabase cosmosDatabase;

  @Mock
  private CosmosContainer cosmosContainer;

  @InjectMocks
  private AgentsController mockController;

  @Autowired
  private AgentsController integController;


  @Test
  public void testGetData() {
    // Arrange
    when(cosmosClient.getDatabase(anyString())).thenReturn(cosmosDatabase);
    when(cosmosDatabase.getContainer(anyString())).thenReturn(cosmosContainer);

    String mockData = "[{\"id\": \"1\", \"name\": \"Mario\"}, {\"id\": \"2\", \"name\": \"Luigi\"}]";
    when(cosmosContainer.queryItems(anyString(), any(), eq(String.class))).thenReturn(mockData);

    // Act
    String result = mockController.getData("Lycaon");

    // Assert
    assertEquals(mockData, result);
  }

  @Test
  public void testGetDataIntegration() {
      // Act
      String result = integController.getData("Lycaon");

      // Assert
      // Verify with actual data in your Cosmos DB
      assertTrue(result.contains("Mario"));
      assertTrue(result.contains("Luigi"));
  }
}

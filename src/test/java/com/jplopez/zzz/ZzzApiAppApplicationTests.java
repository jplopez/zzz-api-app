package com.jplopez.zzz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import com.jplopez.zzz.app.ZzzApiAppApplication;
import com.jplopez.zzz.controllers.AgentsController;
import com.jplopez.zzz.repositories.AgentsRepository;


@SpringBootTest(classes = ZzzApiAppApplication.class)
class ZzzApiAppApplicationTests {

	@Autowired
	Environment env;

	@Autowired
	AgentsController controller;

	@Autowired
	AgentsRepository repo;

	@Value("${server.port}")
	int serverPort;

	@Test
	void contextLoads() {
		assertEquals(8081, serverPort);
		assertNotNull(controller);
		assertNotNull(repo);
		//tests the Spring Boot context load
	}

}

package com.jplopez.zzz;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.jplopez.zzz.app.ZzzApiAppApplication;
import com.jplopez.zzz.controllers.AgentsController;
import com.jplopez.zzz.controllers.BangbooController;
import com.jplopez.zzz.controllers.DiscDriveController;
import com.jplopez.zzz.controllers.DiscDriveSetController;
import com.jplopez.zzz.controllers.WEngineController;

@SpringBootTest(classes = ZzzApiAppApplication.class)
class ZzzApiAppApplicationTests {

  @Autowired
  private ApplicationContext context;

  @Value("${server.port}")
  private int serverPort;

  @Test
  void contextLoads() {
    assertThat(serverPort).isEqualTo(8081);
    assertThat(context.getBeanNamesForType(AgentsController.class)).hasSize(1);
    assertThat(context.getBeanNamesForType(WEngineController.class)).hasSize(1);
    assertThat(context.getBeanNamesForType(BangbooController.class)).hasSize(1);
    assertThat(context.getBeanNamesForType(DiscDriveController.class)).hasSize(1);
    assertThat(context.getBeanNamesForType(DiscDriveSetController.class)).hasSize(1);
  }
}

package com.jplopez.zzz.discsimulation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.jplopez.zzz.app.ZzzApiAppApplication;

@SpringBootTest(classes = ZzzApiAppApplication.class, properties = "zzz.disc-simulation.requests-per-minute=1")
@AutoConfigureMockMvc
class DiscSimulationRateLimitTest {

  @Autowired
  private MockMvc mvc;

  @Test
  void rateLimitsPerClientAndLeavesCatalogReadsAlone() throws Exception {
    String path = "/v1/disc-rules";
    mvc.perform(get(path)).andExpect(status().isOk());
    var limited = mvc.perform(get(path)).andReturn();
    if (limited.getResponse().getStatus() == 200) {
      // Allow the wall-clock rate window to change between the first two requests.
      limited = mvc.perform(get(path)).andReturn();
    }
    status().isTooManyRequests().match(limited);
    content().contentTypeCompatibleWith("application/problem+json").match(limited);
    header().exists("Retry-After").match(limited);
    jsonPath("$.type").value("rate-limited").match(limited);
    mvc.perform(get(path).with(request -> {
      request.setRemoteAddr("192.0.2.2");
      return request;
    })).andExpect(status().isOk());
    mvc.perform(get("/discdrives/4")).andExpect(status().isOk());
  }
}

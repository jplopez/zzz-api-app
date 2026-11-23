package com.jplopez.zzz.discsimulation;

import static com.jplopez.zzz.discsimulation.DiscSimulation.SESSION_PATH;
import static com.jplopez.zzz.discsimulation.DiscSimulationController.COMPARISON_PATH;
import static com.jplopez.zzz.discsimulation.DiscSimulationController.OPENAPI_PATH;
import static com.jplopez.zzz.discsimulation.DiscSimulationController.RULES_PATH;
import static com.jplopez.zzz.discsimulation.DiscSimulationController.RULES_VERSION_ATTRIBUTE;
import static com.jplopez.zzz.discsimulation.DiscSimulationController.SCHEMA_PATH;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration(proxyBeanMethods = false)
class DiscSimulationWebConfig implements WebMvcConfigurer {

  private static final Logger LOG = LoggerFactory.getLogger(DiscSimulationWebConfig.class);
  private static final String STARTED = DiscSimulationWebConfig.class.getName() + ".started";
  private static final String[] PATHS = { RULES_PATH, SCHEMA_PATH, OPENAPI_PATH, COMPARISON_PATH, SESSION_PATH,
      SESSION_PATH + "/**" };

  private final String[] origins;
  private final RateLimiter limiter;

  DiscSimulationWebConfig(@Value("${zzz.disc-simulation.allowed-origins:*}") String[] origins,
                          @Value("${zzz.disc-simulation.requests-per-minute:120}") int requestsPerMinute) {
    this.origins = origins;
    this.limiter = new RateLimiter(requestsPerMinute, Clock.systemUTC());
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    for (String path : PATHS) {
      registry.addMapping(path).allowedOrigins(origins).allowedMethods("GET", "HEAD", "POST", "OPTIONS")
          .allowedHeaders(HttpHeaders.ACCEPT, HttpHeaders.CONTENT_TYPE, HttpHeaders.IF_NONE_MATCH)
          .exposedHeaders(HttpHeaders.ETAG, HttpHeaders.RETRY_AFTER).allowCredentials(false).maxAge(3600);
    }
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new HandlerInterceptor() {
      @Override
      public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(STARTED, System.nanoTime());
        if ("POST".equals(request.getMethod())) {
          response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        }
        if ("OPTIONS".equals(request.getMethod())) {
          return true;
        }
        int retryAfter = limiter.retryAfter(request.getRemoteAddr());
        if (retryAfter == 0) {
          return true;
        }
        DiscProblem problem = DiscProblem.rateLimited(retryAfter);
        response.setStatus(problem.status.value());
        response.setHeader(HttpHeaders.RETRY_AFTER, Integer.toString(problem.retryAfter));
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        DiscJson.MAPPER.writeValue(response.getOutputStream(), problem.body());
        log(request);
        return false;
      }

      @Override
      public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                  Exception exception) {
        log(request);
      }
    }).addPathPatterns(PATHS);
  }

  private static void log(HttpServletRequest request) {
    long started = (Long) request.getAttribute(STARTED);
    LOG.info("disc-simulation endpoint=\"{} {}\" rules_version={} latency_ms={}", request.getMethod(),
        request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE), request.getAttribute(RULES_VERSION_ATTRIBUTE),
        TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
  }

  /** Bounded, instance-local counters; only ephemeral salted client keys are stored. */
  static final class RateLimiter {
    private static final long WINDOW_MILLIS = 60_000;
    private static final int MAX_CLIENTS = 16_384;

    private final int limit;
    private final Clock clock;
    private final byte[] salt = new byte[16];
    private final Map<Long, Integer> counters = new HashMap<>();
    private long window = Long.MIN_VALUE;

    RateLimiter(int limit, Clock clock) {
      if (limit < 1) {
        throw new IllegalArgumentException("Simulation rate limit must be positive");
      }
      this.limit = limit;
      this.clock = clock;
      new SecureRandom().nextBytes(salt);
    }

    int retryAfter(String address) {
      var digest = DiscJson.sha256();
      digest.update(salt);
      long key = ByteBuffer.wrap(digest.digest(address.getBytes(StandardCharsets.UTF_8))).getLong();
      return count(key);
    }

    private synchronized int count(long key) {
      long millis = clock.millis();
      long current = Math.floorDiv(millis, WINDOW_MILLIS);
      if (window != current) {
        counters.clear();
        window = current;
      }
      Integer count = counters.get(key);
      if (count == null ? counters.size() >= MAX_CLIENTS : count >= limit) {
        return (int) ((WINDOW_MILLIS - Math.floorMod(millis, WINDOW_MILLIS) + 999) / 1000);
      }
      counters.put(key, count == null ? 1 : count + 1);
      return 0;
    }
  }
}

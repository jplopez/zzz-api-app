package com.jplopez.zzz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.jplopez.zzz.repositories"})
@EntityScan("com.jplopez.zzz.entities")
@SpringBootApplication(scanBasePackages = {"com.jplopez.zzz"})
public class ZzzApiAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZzzApiAppApplication.class, args);
	}

}

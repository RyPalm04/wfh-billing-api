package com.palmer.wfhbillingapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class WfhBillingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WfhBillingApiApplication.class, args);
	}

}

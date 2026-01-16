package com.example.passwordpolicyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PasswordPolicyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PasswordPolicyServiceApplication.class, args);
	}

}

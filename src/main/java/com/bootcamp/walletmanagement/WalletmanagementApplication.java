package com.bootcamp.walletmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WalletmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletmanagementApplication.class, args);
	}

}

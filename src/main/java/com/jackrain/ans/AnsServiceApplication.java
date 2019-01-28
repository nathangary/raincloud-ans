package com.jackrain.ans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AnsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnsServiceApplication.class, args);
    }

}


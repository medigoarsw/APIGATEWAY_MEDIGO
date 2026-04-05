package com.medigo.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del API Gateway de MediGo.
 * Puerto por defecto: 8081
 */
@SpringBootApplication
public class MedigoApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedigoApiGatewayApplication.class, args);
    }
}

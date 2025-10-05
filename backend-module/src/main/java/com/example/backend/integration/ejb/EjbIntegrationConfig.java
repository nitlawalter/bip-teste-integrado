package com.example.backend.integration.ejb;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BeneficioEjbProperties.class)
public class EjbIntegrationConfig {
}

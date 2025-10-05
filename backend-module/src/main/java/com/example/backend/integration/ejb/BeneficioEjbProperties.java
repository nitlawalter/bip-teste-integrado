package com.example.backend.integration.ejb;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "ejb.beneficio")
public class BeneficioEjbProperties {

    /** Enable JNDI lookup of the Beneficio EJB. */
    private boolean enabled = false;

    /** Fully qualified JNDI name (java:global/...) */
    private String jndiName = "java:global/beneficio/BeneficioEjbService";

    /** JNDI environment properties (factory, provider URL, etc). */
    private Map<String, String> jndi = new HashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public Map<String, String> getJndi() {
        return jndi;
    }

    public void setJndi(Map<String, String> jndi) {
        this.jndi = jndi;
    }
}

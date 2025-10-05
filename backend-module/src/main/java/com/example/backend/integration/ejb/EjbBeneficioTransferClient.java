package com.example.backend.integration.ejb;

import com.example.backend.integration.BeneficioTransferPort;
import com.example.ejb.BeneficioTransferRemote;
import jakarta.ejb.EJBException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Hashtable;

@Component
@ConditionalOnProperty(prefix = "ejb.beneficio", name = "enabled", havingValue = "true")
public class EjbBeneficioTransferClient implements BeneficioTransferPort {

    private static final Logger log = LoggerFactory.getLogger(EjbBeneficioTransferClient.class);

    private final BeneficioEjbProperties properties;

    public EjbBeneficioTransferClient(BeneficioEjbProperties properties) {
        this.properties = properties;
    }

    @Override
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        BeneficioTransferRemote ejb = lookup();
        try {
            ejb.transfer(fromId, toId, amount);
        } catch (EJBException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException runtime) {
                throw runtime;
            }
            throw new IllegalStateException("A execução do EJB falhou", ex);
        }
    }

    private BeneficioTransferRemote lookup() {
        Hashtable<String, Object> env = new Hashtable<>();
        properties.getJndi().forEach(env::put);
        try {
            InitialContext context = env.isEmpty() ? new InitialContext() : new InitialContext(env);
            Object instance = context.lookup(properties.getJndiName());
            return (BeneficioTransferRemote) instance;
        } catch (NamingException ex) {
            log.error("Falha ao consultar Beneficio EJB em {}", properties.getJndiName(), ex);
            throw new IllegalStateException("Não é possível consultar o Beneficio EJB", ex);
        }
    }
}

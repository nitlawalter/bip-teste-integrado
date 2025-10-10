package com.example.backend.integration;

import com.example.backend.model.Beneficio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
@ConditionalOnProperty(prefix = "ejb.beneficio", name = "enabled", havingValue = "false", matchIfMissing = true)
public class LocalBeneficioTransferService implements BeneficioTransferPort {

    @PersistenceContext
    private EntityManager em;
    private static final Logger log = LoggerFactory.getLogger(LocalBeneficioTransferService.class);

    @Transactional
    @Override
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        if (fromId == null || toId == null) throw new IllegalArgumentException("IDs devem ser fornecidos");
        if (fromId.equals(toId)) throw new IllegalArgumentException("fromId and toId devem ser diferentes");
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("O valor deve ser positivo");

        log.debug("Starting local transfer: from={} to={} amount={}", fromId, toId, amount);
        Beneficio from = em.find(Beneficio.class, fromId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        Beneficio to = em.find(Beneficio.class, toId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        if (from == null || to == null) {
            throw new NoSuchElementException("Beneficio não encontrado");
        }
        if (Boolean.FALSE.equals(from.getAtivo()) || Boolean.FALSE.equals(to.getAtivo())) {
            throw new IllegalStateException("Ambos os Benefícios devem estar ativos");
        }
        if (from.getValor().compareTo(amount) < 0) {
            throw new IllegalStateException("Saldo insuficiente");
        }

        from.setValor(from.getValor().subtract(amount));
        to.setValor(to.getValor().add(amount));

        em.merge(from);
        em.merge(to);
        em.flush();

        log.info("Transfer completed: from={} to={} amount={}", fromId, toId, amount);
    }
}

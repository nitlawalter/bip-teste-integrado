package com.example.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Stateless
public class BeneficioEjbService implements BeneficioTransferRemote {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        if (fromId == null || toId == null) throw new IllegalArgumentException("IDs obrigatórios");
        if (fromId.equals(toId)) throw new IllegalArgumentException("IDs de origem e destino devem ser diferentes");
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Valor deve ser positivo");

        Beneficio from = em.find(Beneficio.class, fromId, LockModeType.OPTIMISTIC);
        Beneficio to   = em.find(Beneficio.class, toId, LockModeType.OPTIMISTIC);

        if (from == null || to == null) throw new NoSuchElementException("Benefício não encontrado");
        if (Boolean.FALSE.equals(from.getAtivo()) || Boolean.FALSE.equals(to.getAtivo()))
            throw new IllegalStateException("Ambos os benefícios precisam estar ativos");
        if (from.getValor().compareTo(amount) < 0) throw new IllegalStateException("Saldo insuficiente");

        from.setValor(from.getValor().subtract(amount));
        to.setValor(to.getValor().add(amount));

        em.merge(from);
        em.merge(to);
        em.flush();
    }
}

package com.example.ejb;

import jakarta.ejb.Remote;

import java.math.BigDecimal;

@Remote
public interface BeneficioTransferRemote {
    void transfer(Long fromId, Long toId, BigDecimal amount);
}

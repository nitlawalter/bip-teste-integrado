package com.example.backend.integration;

import java.math.BigDecimal;

public interface BeneficioTransferPort {
    void transfer(Long fromId, Long toId, BigDecimal amount);
}

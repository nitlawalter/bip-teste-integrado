package com.example.backend.dto;

import java.math.BigDecimal;

public class TransferRequest {

    private Long fromId;
    private Long toId;
    private BigDecimal amount;

    public TransferRequest() {
    }

    public TransferRequest(Long fromId, Long toId, BigDecimal amount) {
        this.fromId = fromId;
        this.toId = toId;
        this.amount = amount;
    }

    public Long getFromId() {
        return fromId;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public Long getToId() {
        return toId;
    }

    public void setToId(Long toId) {
        this.toId = toId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}


package com.example.backend.dto;

import com.example.backend.model.Beneficio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class BeneficioRequest {

    @NotBlank
    private String nome;

    private String descricao;

    @NotNull
    private BigDecimal valor;

    private Boolean ativo;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Beneficio toEntity() {
        Beneficio beneficio = new Beneficio();
        beneficio.setNome(this.getNome());
        beneficio.setDescricao(this.getDescricao());
        beneficio.setValor(this.getValor());
        beneficio.setAtivo(this.getAtivo());
        return beneficio;
    }
}

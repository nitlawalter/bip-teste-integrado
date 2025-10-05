package com.example.backend.service;

import com.example.backend.integration.BeneficioTransferPort;
import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BeneficioService {

    private final BeneficioRepository repository;
    private final BeneficioTransferPort transferPort;

    public BeneficioService(BeneficioRepository repository, BeneficioTransferPort transferPort) {
        this.repository = repository;
        this.transferPort = transferPort;
    }

    public List<Beneficio> listAll() {
        return repository.findAll();
    }

    public Beneficio getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Beneficio não encontrado: " + id));
    }

    public Beneficio create(Beneficio b) {
        if (b.getValor() == null) b.setValor(BigDecimal.ZERO);
        if (b.getAtivo() == null) b.setAtivo(Boolean.TRUE);
        return repository.save(b);
    }

    public Beneficio update(Long id, Beneficio changes) {
        Beneficio current = getById(id);
        current.setNome(changes.getNome());
        current.setDescricao(changes.getDescricao());
        current.setValor(changes.getValor());
        current.setAtivo(changes.getAtivo());
        return repository.save(current);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        transferPort.transfer(fromId, toId, amount);
    }
}

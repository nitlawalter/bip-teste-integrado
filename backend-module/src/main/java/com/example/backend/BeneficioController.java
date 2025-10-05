package com.example.backend;

import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.backend.model.Beneficio;
import com.example.backend.service.BeneficioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/beneficios")
public class BeneficioController {

    private final BeneficioService service;

    public BeneficioController(BeneficioService service) {
        this.service = service;
    }

    @GetMapping
    public List<BeneficioResponse> list() {
        return service.listAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public BeneficioResponse get(@PathVariable Long id) {
        return toResponse(service.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeneficioResponse create(@Valid @RequestBody BeneficioRequest request) {
        Beneficio created = service.create(toEntity(request));
        return toResponse(created);
    }

    @PutMapping("/{id}")
    public BeneficioResponse update(@PathVariable Long id, @Valid @RequestBody BeneficioRequest request) {
        Beneficio updated = service.update(id, toEntity(request));
        return toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    public static class TransferRequest {
        public Long fromId;
        public Long toId;
        public BigDecimal amount;
        public Long getFromId() { return fromId; }
        public Long getToId() { return toId; }
        public BigDecimal getAmount() { return amount; }
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@RequestBody TransferRequest req) {
        service.transfer(req.getFromId(), req.getToId(), req.getAmount());
    }

    private Beneficio toEntity(BeneficioRequest request) {
        Beneficio beneficio = new Beneficio();
        beneficio.setNome(request.getNome());
        beneficio.setDescricao(request.getDescricao());
        beneficio.setValor(request.getValor());
        beneficio.setAtivo(request.getAtivo());
        return beneficio;
    }

    private BeneficioResponse toResponse(Beneficio beneficio) {
        return new BeneficioResponse(
                beneficio.getId(),
                beneficio.getNome(),
                beneficio.getDescricao(),
                beneficio.getValor(),
                beneficio.getAtivo()
        );
    }
}

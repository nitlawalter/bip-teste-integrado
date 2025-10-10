package com.example.backend.controller;

import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.backend.dto.TransferRequest;
import com.example.backend.model.Beneficio;
import com.example.backend.service.BeneficioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/beneficios")
public class BeneficioController {

    private final BeneficioService service;
    private static final Logger log = LoggerFactory.getLogger(BeneficioController.class);

    public BeneficioController(BeneficioService service) {
        this.service = service;
    }

    @GetMapping
    public List<BeneficioResponse> list() {
        return service.listAll().stream()
                .map(BeneficioResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public BeneficioResponse get(@PathVariable Long id) {
        return BeneficioResponse.from(service.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeneficioResponse create(@Valid @RequestBody BeneficioRequest request) {
        Beneficio created = service.create(request.toEntity());
        return BeneficioResponse.from(created);
    }

    @PutMapping("/{id}")
    public BeneficioResponse update(@PathVariable Long id, @Valid @RequestBody BeneficioRequest request) {
        Beneficio updated = service.update(id, request.toEntity());
        return BeneficioResponse.from(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@RequestBody TransferRequest req) {
        log.info("Transfer requested: from={} to={} amount={}", req.getFromId(), req.getToId(), req.getAmount());
        service.transfer(req.getFromId(), req.getToId(), req.getAmount());
    }

}

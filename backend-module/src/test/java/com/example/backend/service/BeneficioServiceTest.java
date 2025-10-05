package com.example.backend.service;

import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BeneficioServiceTest {

    @Autowired
    private BeneficioService service;

    @Autowired
    private BeneficioRepository repository;

    @Test
    @Transactional
    void transferShouldMoveFundsBetweenBeneficios() {
        service.transfer(1L, 2L, new BigDecimal("150.00"));

        Beneficio from = repository.findById(1L).orElseThrow();
        Beneficio to = repository.findById(2L).orElseThrow();

        assertThat(from.getValor()).isEqualByComparingTo("850.00");
        assertThat(to.getValor()).isEqualByComparingTo("650.00");
    }

    @Test
    void transferShouldFailWhenInsufficientBalance() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.transfer(2L, 1L, new BigDecimal("1000.00")));

        assertThat(exception.getMessage()).containsIgnoringCase("Saldo insuficiente");
    }
}

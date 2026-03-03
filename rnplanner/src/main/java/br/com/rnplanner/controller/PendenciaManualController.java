package br.com.rnplanner.controller;

import br.com.rnplanner.model.PendenciaManual;
import br.com.rnplanner.repository.PendenciaManualRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/pendencias-manuais")
@CrossOrigin(origins = "*")
public class PendenciaManualController {

    private final PendenciaManualRepository repository;

    public PendenciaManualController(PendenciaManualRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/salvar")
    public ResponseEntity<PendenciaManual> salvarPendenciaAvulsa(@RequestBody PendenciaManual pendencia) {
        pendencia.setData(LocalDate.now());
        pendencia.setStatus("PENDENTE"); // Força o status inicial
        return ResponseEntity.ok(repository.save(pendencia));
    }
}
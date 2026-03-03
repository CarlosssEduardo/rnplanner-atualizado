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

    private final PendenciaManualRepository pendenciaManualRepository;

    public PendenciaManualController(PendenciaManualRepository pendenciaManualRepository) {
        this.pendenciaManualRepository = pendenciaManualRepository;
    }

    @PostMapping("/salvar")
    public ResponseEntity<PendenciaManual> salvarPendenciaAvulsa(@RequestBody PendenciaManual pendencia) {
        pendencia.setData(LocalDate.now());
        pendencia.setStatus("PENDENTE");
        return ResponseEntity.ok(pendenciaManualRepository.save(pendencia));
    }

    @PutMapping("/resolver/{id}")
    public ResponseEntity<String> resolver(@PathVariable Long id) {
        pendenciaManualRepository.findById(id).ifPresent(p -> {
            p.setStatus("RESOLVIDO");
            pendenciaManualRepository.save(p);
        });
        return ResponseEntity.ok("Pendência resolvida!");
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        pendenciaManualRepository.deleteById(id);
        return ResponseEntity.ok("Pendência removida!");
    }
}
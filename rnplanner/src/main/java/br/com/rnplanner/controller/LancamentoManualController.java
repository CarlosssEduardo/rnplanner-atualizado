package br.com.rnplanner.controller;

import br.com.rnplanner.model.LancamentoManual;
import br.com.rnplanner.repository.LancamentoManualRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/lancamento-manual")
@CrossOrigin(origins = "*")
public class LancamentoManualController {

    private final LancamentoManualRepository repository;

    public LancamentoManualController(LancamentoManualRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/salvar")
    public ResponseEntity<LancamentoManual> salvar(@RequestBody LancamentoManual lancamento) {
        lancamento.setData(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
        return ResponseEntity.ok(repository.save(lancamento));
    }

    @GetMapping("/listar/{setor}")
    public ResponseEntity<List<LancamentoManual>> listarPorSetor(@PathVariable String setor) {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        return ResponseEntity.ok(repository.findBySetorAndData(setor, hoje));
    }
}
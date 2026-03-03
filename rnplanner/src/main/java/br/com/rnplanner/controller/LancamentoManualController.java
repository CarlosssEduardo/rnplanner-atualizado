package br.com.rnplanner.controller;

import br.com.rnplanner.model.LancamentoManual;
import br.com.rnplanner.repository.LancamentoManualRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/lancamento-manual")
@CrossOrigin(origins = "*")
public class LancamentoManualController {

    private final LancamentoManualRepository repository;

    public LancamentoManualController(LancamentoManualRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/salvar")
    public ResponseEntity<String> salvarManual(@RequestBody LancamentoManual lancamentoRecebido) {
        LocalDate hoje = LocalDate.now();

        // Procura se já tem um cofre manual hoje, se não, cria um
        LancamentoManual registro = repository.findBySetorAndData(lancamentoRecebido.getSetor(), hoje);
        if (registro == null) {
            registro = new LancamentoManual();
            registro.setSetor(lancamentoRecebido.getSetor());
            registro.setData(hoje);
        }

        // Soma o que já tinha com o que ele acabou de digitar
        registro.setTasks(registro.getTasks() + lancamentoRecebido.getTasks());
        registro.setOfertas(registro.getOfertas() + lancamentoRecebido.getOfertas());
        registro.setMissoes(registro.getMissoes() + lancamentoRecebido.getMissoes());

        repository.save(registro);
        return ResponseEntity.ok("Lançamento manual registrado com sucesso!");
    }
}
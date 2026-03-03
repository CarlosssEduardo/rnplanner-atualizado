package br.com.rnplanner.controller;

import br.com.rnplanner.model.SetorPermitido;
import br.com.rnplanner.repository.SetorPermitidoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setores-vip")
@CrossOrigin(origins = "*") // Permite o React acessar essa rota
public class SetorPermitidoController {

    private final SetorPermitidoRepository repository;

    public SetorPermitidoController(SetorPermitidoRepository repository) {
        this.repository = repository;
    }

    // O Painel Admin chama isso para listar quem já está liberado
    @GetMapping
    public ResponseEntity<List<SetorPermitido>> listarTodos() {
        return ResponseEntity.ok(repository.findAll());
    }

    // O Painel Admin chama isso quando você clica em "Liberar Acesso"
    @PostMapping("/adicionar/{setor}")
    public ResponseEntity<String> adicionarSetor(@PathVariable String setor) {
        if (repository.existsBySetor(setor)) {
            return ResponseEntity.badRequest().body("Setor já está na lista VIP!");
        }
        SetorPermitido novo = new SetorPermitido();
        novo.setSetor(setor);
        repository.save(novo);
        return ResponseEntity.ok("Setor " + setor + " liberado com sucesso!");
    }

    // O Painel Admin chama isso para revogar o acesso de alguém
    @DeleteMapping("/remover/{id}")
    public ResponseEntity<String> removerSetor(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok("Acesso revogado!");
    }
}
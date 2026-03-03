package br.com.rnplanner.controller;

import br.com.rnplanner.model.Pdv;
import br.com.rnplanner.service.PdvService;
import br.com.rnplanner.repository.PdvRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/pdvs")
@CrossOrigin(origins = "*")
public class PdvController {

    private final PdvService pdvService;
    // 🔥 ERRO 1 RESOLVIDO: Injetamos o Repositório aqui!
    private final PdvRepository pdvRepository;

    // 🔥 O Construtor agora recebe o Service e o Repository
    public PdvController(PdvService pdvService, PdvRepository pdvRepository) {
        this.pdvService = pdvService;
        this.pdvRepository = pdvRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            pdvService.importarExcel(file);
            return ResponseEntity.ok("Planilha importada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao importar planilha: " + e.getMessage());
        }
    }

    @GetMapping("/dia/{dia}")
    public ResponseEntity<List<Pdv>> listarPorDia(@PathVariable String dia) {
        return ResponseEntity.ok(pdvService.listarPorDia(dia));
    }

    // 🔥 A Rota exclusiva de listar por Setor
    @GetMapping("/setor/{setor}")
    public ResponseEntity<List<Pdv>> listarPdvsPorSetor(@PathVariable String setor) {
        return ResponseEntity.ok(pdvRepository.findBySetor(setor));
    }

    // 🔥 ROTA DO SEGURANÇA: Retorna TRUE se o setor existir no banco
    @GetMapping("/verificar/{setor}")
    public ResponseEntity<Boolean> verificarSetor(@PathVariable String setor) {
        return ResponseEntity.ok(pdvRepository.existsBySetor(setor));
    }
}
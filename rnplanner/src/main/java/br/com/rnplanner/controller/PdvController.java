package br.com.rnplanner.controller;

import br.com.rnplanner.model.Pdv;
import br.com.rnplanner.repository.PdvRepository;
import br.com.rnplanner.service.PdvService; // Você vai criar esse service
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/pdvs")
public class PdvController {

    private final PdvRepository repository;
    private final PdvService pdvService; // Adicionamos o service aqui

    // Atualizamos o construtor para injetar ambos
    public PdvController(PdvRepository repository, PdvService pdvService) {
        this.repository = repository;
        this.pdvService = pdvService;
    }

    @PostMapping
    public Pdv criar(@RequestBody Pdv pdv) {
        return repository.save(pdv);
    }

    @GetMapping
    public List<Pdv> listar() {
        return repository.findAll();
    }

    // NOVO: Endpoint para importar o Excel
    @PostMapping("/importar")
    public ResponseEntity<String> importar(@RequestParam("file") MultipartFile file) {
        try {
            pdvService.importarExcel(file);
            return ResponseEntity.ok("PDVs importados com sucesso do Excel!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao importar: " + e.getMessage());
        }
    }
}
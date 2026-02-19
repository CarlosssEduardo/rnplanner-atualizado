package br.com.rnplanner.controller;

// Entidade PDV (tabela do banco)
import br.com.rnplanner.model.Pdv;

// Repository → acesso direto ao banco
import br.com.rnplanner.repository.PdvRepository;

// Service → regra de negócio (importação Excel)
import br.com.rnplanner.service.PdvService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
 * @RestController
 * Diz que essa classe expõe endpoints HTTP
 * (API que o app iPhone / Android vai chamar)
 */
@RestController

/*
 * Prefixo das rotas
 * Tudo aqui começa com /pdvs
 */
@RequestMapping("/pdvs")
public class PdvController {

    /*
     * Repository → operações simples CRUD
     * Service → operações complexas (Excel, lógica)
     */
    private final PdvRepository repository;
    private final PdvService pdvService;

    /*
     * Injeção de dependência
     * Spring cria repository e service e entrega aqui
     */
    public PdvController(PdvRepository repository, PdvService pdvService) {
        this.repository = repository;
        this.pdvService = pdvService;
    }

    /*
     * POST /pdvs
     *
     * Cria um PDV manualmente
     * Recebe JSON no body
     */
    @PostMapping
    public Pdv criar(@RequestBody Pdv pdv) {
        return repository.save(pdv);
    }

    /*
     * GET /pdvs
     *
     * Lista todos os PDVs
     */
    @GetMapping
    public List<Pdv> listar() {
        return repository.findAll();
    }

    /*
     * POST /pdvs/importar
     *
     * Recebe arquivo Excel
     * Chama o Service para processar
     */
    @PostMapping("/importar")
    public ResponseEntity<String> importar(@RequestParam("file") MultipartFile file) {
        try {
            pdvService.importarExcel(file);
            return ResponseEntity.ok("PDVs importados com sucesso do Excel Setor 503!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao importar: " + e.getMessage());
        }
    }
}

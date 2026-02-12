package br.com.rnplanner.controller;

import br.com.rnplanner.model.Visita;
import br.com.rnplanner.service.VisitaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/visitas")
public class VisitaController {

    private final VisitaService visitaService;

    public VisitaController(VisitaService visitaService) {
        this.visitaService = visitaService;
    }

    @PostMapping("/iniciar/{pdvId}")
    public Visita iniciarVisita(@PathVariable Long pdvId) {
        return visitaService.iniciarVisita(pdvId);
    }

    // ✅ ADICIONE ESTE MÉTODO PARA O GET FUNCIONAR
    @GetMapping("/{id}")
    public Visita buscarPorId(@PathVariable Long id) {
        return visitaService.buscarPorId(id);
    }

    // Opcional: Listar todas as visitas do dia
    @GetMapping
    public List<Visita> listarTodas() {
        return visitaService.listarTodas();
    }
}
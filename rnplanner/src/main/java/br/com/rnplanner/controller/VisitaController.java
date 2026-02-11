package br.com.rnplanner.controller;

import br.com.rnplanner.model.Visita;
import br.com.rnplanner.service.VisitaService;
import org.springframework.web.bind.annotation.*;

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
}

package br.com.rnplanner.controller;

import br.com.rnplanner.dto.DashboardDiaDTO;
import br.com.rnplanner.dto.VisitaRelatorioDTO;
import br.com.rnplanner.model.Visita;
import br.com.rnplanner.service.VisitaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Controller de VISITAS
 * Base da API → /visitas
 */
@RestController
@RequestMapping("/visitas")
public class VisitaController {

    /*
     * Service → onde fica a regra de negócio real
     */
    private final VisitaService visitaService;

    /*
     * Injeção de dependência
     */
    public VisitaController(VisitaService visitaService) {
        this.visitaService = visitaService;
    }

    /*
     * 1️⃣ INICIAR VISITA
     * POST /visitas/iniciar/{pdvId}
     *
     * Cria uma nova visita para aquele PDV
     * Já traz o histórico da última visita
     */
    @PostMapping("/iniciar/{pdvId}")
    public Visita iniciarVisita(@PathVariable Long pdvId) {
        return visitaService.iniciarVisita(pdvId);
    }

    /*
     * 2️⃣ RESUMO DO PDV
     * GET /visitas/{id}/resumo
     *
     * Mostra quantas tasks, missões e ofertas foram feitas
     */
    @GetMapping("/{id}/resumo")
    public VisitaRelatorioDTO verResumo(@PathVariable Long id) {
        return visitaService.obterResumo(id);
    }

    /*
     * 3️⃣ FINALIZAR VISITA
     * PUT /visitas/{id}/finalizar
     *
     * Salva a anotação final e encerra a visita
     */
    @PutMapping(value = "/{id}/finalizar", consumes = "text/plain")
    public Visita finalizar(@PathVariable Long id, @RequestBody String anotacao) {
        return visitaService.finalizarVisita(id, anotacao);
    }

    /*
     * 4️⃣ DASHBOARD GERAL DO DIA
     * GET /visitas/dashboard/geral
     *
     * Mostra produtividade acumulada
     */
    @GetMapping("/dashboard/geral")
    public DashboardDiaDTO verDashboardGeral() {
        return visitaService.obterDashboardGeral();
    }

    /*
     * 5️⃣ UTILITÁRIOS
     */

    // Buscar visita específica
    @GetMapping("/{id}")
    public Visita buscarPorId(@PathVariable Long id) {
        return visitaService.buscarPorId(id);
    }

    // Listar todas visitas
    @GetMapping
    public List<Visita> listarTodas() {
        return visitaService.listarTodas();
    }
}

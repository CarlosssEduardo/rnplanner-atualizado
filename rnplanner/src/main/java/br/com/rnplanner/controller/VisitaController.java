package br.com.rnplanner.controller;

import br.com.rnplanner.dto.DashboardDiaDTO;
import br.com.rnplanner.dto.PendenciaDTO;
import br.com.rnplanner.dto.FinalizarVisitaDTO;
import br.com.rnplanner.dto.ResumoMesDTO;
import br.com.rnplanner.dto.VisitaRelatorioDTO;
import br.com.rnplanner.model.Visita;
import br.com.rnplanner.service.VisitaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/visitas")
@CrossOrigin(origins = "*")
public class VisitaController {

    private final VisitaService visitaService;

    public VisitaController(VisitaService visitaService) {
        this.visitaService = visitaService;
    }

    @PostMapping("/iniciar/{pdvId}")
    public ResponseEntity<Visita> iniciarVisita(@PathVariable Long pdvId) {
        Visita visita = visitaService.iniciarVisita(pdvId);
        return ResponseEntity.ok(visita);
    }

    @PutMapping(value = "/{id}/finalizar", consumes = "application/json")
    public ResponseEntity<Visita> finalizar(@PathVariable Long id, @RequestBody FinalizarVisitaDTO dto) {
        Visita visitaFinalizada = visitaService.finalizarVisita(
                id,
                dto.getAnotacao(),
                dto.getQtdTasks(),
                dto.getQtdOfertas(),
                dto.getQtdMissoes()
        );
        return ResponseEntity.ok(visitaFinalizada);
    }

    // 🔥 ROTA BLINDADA: O celular tem que avisar o setor! (Ex: /visitas/dashboard/501)
    @GetMapping("/dashboard/{setor}")
    public ResponseEntity<DashboardDiaDTO> obterDashboardGeral(@PathVariable String setor) {
        DashboardDiaDTO dashboard = visitaService.obterDashboardDoDiaPorSetor(setor);
        return ResponseEntity.ok(dashboard);
    }

    // 🔥 NOVO ENDPOINT: Dashboard Acumulado do Mês
    @GetMapping("/dashboard/mes")
    public ResponseEntity<ResumoMesDTO> obterDashboardMes() {
        return ResponseEntity.ok(visitaService.obterResumoMes());
    }

    @GetMapping
    public ResponseEntity<List<Visita>> listarTodas() {
        return ResponseEntity.ok(visitaService.listarTodas());
    }

    @GetMapping("/{id}/resumo")
    public ResponseEntity<VisitaRelatorioDTO> obterResumo(@PathVariable Long id) {
        return ResponseEntity.ok(visitaService.obterResumo(id));
    }

    // 🔥 ROTA BLINDADA: Pendências apenas do setor! (Ex: /visitas/pendencias/501)
    @GetMapping("/pendencias/{setor}")
    public ResponseEntity<List<PendenciaDTO>> listarPendenciasGlobais(@PathVariable String setor) {
        return ResponseEntity.ok(visitaService.listarPendenciasGlobaisPorSetor(setor));
    }
}
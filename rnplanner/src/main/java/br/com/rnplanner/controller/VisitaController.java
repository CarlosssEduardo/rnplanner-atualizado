package br.com.rnplanner.controller;

import br.com.rnplanner.dto.DashboardDiaDTO;
import br.com.rnplanner.dto.PendenciaDTO;
import br.com.rnplanner.dto.FinalizarVisitaDTO;
import br.com.rnplanner.dto.ResumoMesDTO;
import br.com.rnplanner.dto.VisitaRelatorioDTO;
import br.com.rnplanner.model.Visita;
import br.com.rnplanner.model.PendenciaManual; // 🔥 NOVO IMPORT
import br.com.rnplanner.service.VisitaService;
import br.com.rnplanner.repository.PendenciaManualRepository; // 🔥 NOVO IMPORT
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList; // 🔥 NOVO IMPORT
import java.util.List;

@RestController
@RequestMapping("/visitas")
@CrossOrigin(origins = "*")
public class VisitaController {

    private final VisitaService visitaService;
    private final PendenciaManualRepository pendenciaManualRepository; // 🔥 INJETANDO A TORNEIRA NOVA

    // 🔥 CONSTRUTOR ATUALIZADO
    public VisitaController(VisitaService visitaService, PendenciaManualRepository pendenciaManualRepository) {
        this.visitaService = visitaService;
        this.pendenciaManualRepository = pendenciaManualRepository;
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

    // 🔥 ROTA BLINDADA E MISTURADA: Pendências apenas do setor! (Ex: /visitas/pendencias/501)
    @GetMapping("/pendencias/{setor}")
    public ResponseEntity<List<PendenciaDTO>> listarPendenciasGlobais(@PathVariable String setor) {

        // 1. Torneira Oficial (Vem dos PDVs)
        List<PendenciaDTO> pendencias = new ArrayList<>(visitaService.listarPendenciasGlobaisPorSetor(setor));

        // 2. Torneira Manual (Vem das anotações da rua)
        List<PendenciaManual> manuais = pendenciaManualRepository.findBySetorAndStatus(setor, "PENDENTE");

        // 3. O Disfarce: Transforma a anotação manual em um "PendenciaDTO" para o React entender
        for (PendenciaManual pm : manuais) {
            PendenciaDTO dto = new PendenciaDTO();
            dto.setId("MANUAL-" + pm.getId()); // ID falso só pro React não bugar a lista
            dto.setPdvId(0L); // Zero indica que não tem loja vinculada
            dto.setPdvNome("Anotação Avulsa"); // Vai aparecer isso destacado no aplicativo!
            dto.setTexto(pm.getTexto());
            dto.setStatus(pm.getStatus());

            pendencias.add(dto); // Adiciona na lista principal
        }

        // Devolve tudo misturado pro Front-end!
        return ResponseEntity.ok(pendencias);
    }
}
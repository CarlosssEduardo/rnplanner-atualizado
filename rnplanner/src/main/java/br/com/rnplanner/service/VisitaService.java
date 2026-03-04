package br.com.rnplanner.service;

import br.com.rnplanner.dto.*;
import br.com.rnplanner.model.*;
import br.com.rnplanner.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitaService {

    private final VisitaRepository visitaRepository;
    private final PdvRepository pdvRepository;
    private final LancamentoManualRepository lancamentoManualRepository; // 🔥 O ELO PERDIDO: Adicionado para somar o Hub

    public VisitaService(VisitaRepository visitaRepository,
                         PdvRepository pdvRepository,
                         LancamentoManualRepository lancamentoManualRepository) {
        this.visitaRepository = visitaRepository;
        this.pdvRepository = pdvRepository;
        this.lancamentoManualRepository = lancamentoManualRepository;
    }

    public Visita iniciarVisita(Long pdvId) {
        Pdv pdv = pdvRepository.findById(pdvId).orElseThrow();
        Visita visita = new Visita();
        visita.setPdv(pdv);
        // 🕒 BLINDAGEM: Garante a data de Brasília para evitar erro de fuso na Azure
        visita.setData(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
        visita.setFinalizada(false);
        visita.setSetor(pdv.getSetor()); // Carimba o setor direto na visita
        return visitaRepository.save(visita);
    }

    public Visita finalizarVisita(Long id, String anotacao, int tasks, int ofertas, int missoes) {
        Visita visita = visitaRepository.findById(id).orElseThrow();
        visita.setObservacao(anotacao);
        visita.setQtdTasks(tasks);
        visita.setQtdOfertas(ofertas);
        visita.setQtdMissoes(missoes);
        visita.setFinalizada(true);

        // 🕵️ TRADUTOR DO EDUARDO: Identifica se há itens pendentes no JSON do React
        if (anotacao != null && anotacao.contains("\"status\":\"PENDENTE\"")) {
            visita.setPendenciaStatus("PENDENTE");
        } else {
            visita.setPendenciaStatus("RESOLVIDO");
        }
        return visitaRepository.save(visita);
    }

    // 🚀 O FUNIL DO DIA: Soma Visitas + Hub para a barra de 100% andar!
    public DashboardDiaDTO obterDashboardDoDiaPorSetor(String setor) {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

        // Torneira 1: Visitas Oficiais
        long tasksOficiais = visitaRepository.sumTasksByDataAndSetor(hoje, setor);
        long ofertasOficiais = visitaRepository.sumOfertasByDataAndSetor(hoje, setor);
        long missoesOficiais = visitaRepository.sumMissoesByDataAndSetor(hoje, setor);

        // Torneira 2: Hub de Execução (Lançamento Manual)
        long tasksHub = lancamentoManualRepository.sumTasksManuais(hoje, setor);
        long ofertasHub = lancamentoManualRepository.sumOfertasManuais(hoje, setor);
        long missoesHub = lancamentoManualRepository.sumMissoesManuais(hoje, setor);

        // Soma Total
        long tasksTotal = tasksOficiais + tasksHub;
        long ofertasTotal = ofertasOficiais + ofertasHub;
        long missoesTotal = missoesOficiais + missoesHub;

        List<Long> ids = visitaRepository.findPdvIdsVisitadosHojePorSetor(hoje, setor);

        return new DashboardDiaDTO(missoesTotal, tasksTotal, ofertasTotal, ids.size(), ids);
    }

    // 🏆 O RESUMO DO MÊS: Resolve o erro de "0 Dias Trabalhados"
    public ResumoMesDTO obterResumoMes(String setor) {
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate inicio = hoje.withDayOfMonth(1);
        LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());

        // Dados das visitas
        int dias = (int) visitaRepository.countDiasTrabalhadosNoMesPorSetor(inicio, fim, setor);
        long tasksOficiais = visitaRepository.sumTasksNoMesPorSetor(inicio, fim, setor);
        int resolvidos = (int) visitaRepository.countProblemasResolvidosNoMesPorSetor(inicio, fim, setor);

        // Dados do Hub no mês
        long tasksHub = lancamentoManualRepository.sumTasksManuaisNoMes(inicio, fim, setor);

        int totalTasksMes = (int) (tasksOficiais + tasksHub);

        // 🔥 LÓGICA DE ATIVIDADE: Se trabalhou no Hub mas não abriu visita, conta como 1 dia.
        if (dias == 0 && totalTasksMes > 0) {
            dias = 1;
        }

        return new ResumoMesDTO(dias, resolvidos, totalTasksMes, "Top 10 - CDD Belém");
    }

    public List<Visita> listarTodas() {
        return visitaRepository.findAll();
    }

    public VisitaRelatorioDTO obterResumo(Long id) {
        Visita v = visitaRepository.findById(id).orElseThrow();
        return new VisitaRelatorioDTO(v.getPdv().getNome(), v.getObservacao(), v.getQtdTasks(), v.getQtdOfertas(), v.getQtdMissoes());
    }

    // Adicione isso antes do último } do VisitaService.java
    public Visita buscarPorId(Long id) {
        return visitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visita não encontrada"));
    }

    public List<PendenciaDTO> listarPendenciasGlobaisPorSetor(String setor) {
        return visitaRepository.findAll().stream()
                .filter(v -> v.getSetor() != null && v.getSetor().equals(setor))
                .filter(v -> v.getObservacao() != null && !v.getObservacao().trim().isEmpty())
                .map(v -> {
                    PendenciaDTO dto = new PendenciaDTO();
                    dto.setId(v.getId().toString());
                    dto.setPdvId(v.getPdv().getId());
                    dto.setPdvNome(v.getPdv().getNome());
                    dto.setTexto(v.getObservacao());
                    dto.setStatus(v.getPendenciaStatus() != null ? v.getPendenciaStatus() : "PENDENTE");
                    return dto;
                }).collect(Collectors.toList());
    }

    // Adicione este método ao seu VisitaService.java
    public List<String> obterItensPendentes(Long visitaId) {
        Visita v = visitaRepository.findById(visitaId).orElseThrow();
        String obs = v.getObservacao();

        List<String> itens = new ArrayList<>();
        if (obs != null && obs.contains("[")) {
            // 🔥 MÁGICA: Extrai os textos do meio do JSON para a tela de detalhe
            String[] partes = obs.split("\"texto\":\"");
            for (int i = 1; i < partes.length; i++) {
                String texto = partes[i].split("\"")[0];
                itens.add(texto);
            }
        } else if (obs != null && !obs.trim().isEmpty()) {
            itens.add(obs); // Texto simples
        }
        return itens;
    }
}
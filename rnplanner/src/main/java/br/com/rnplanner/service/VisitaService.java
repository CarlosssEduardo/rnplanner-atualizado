package br.com.rnplanner.service;

import br.com.rnplanner.dto.DashboardDiaDTO;
import br.com.rnplanner.dto.VisitaRelatorioDTO;
import br.com.rnplanner.model.Pdv;
import br.com.rnplanner.model.Task.TipoTask;
import br.com.rnplanner.model.Visita;
import br.com.rnplanner.repository.PdvRepository;
import br.com.rnplanner.repository.TaskRepository;
import br.com.rnplanner.repository.VisitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
// Marca como classe de regra de negócio responsável pelas visitas
public class VisitaService {

    private final VisitaRepository visitaRepository;
    // Repositório que salva e busca visitas no banco

    private final PdvRepository pdvRepository;
    // Usado para buscar o PDV quando uma visita começa

    private final TaskRepository taskRepository;
    // Usado para contar tarefas e montar dashboard/relatórios

    // Construtor (injeção de dependência)
    // Spring entrega os repositories prontos
    public VisitaService(VisitaRepository visitaRepository,
                         PdvRepository pdvRepository,
                         TaskRepository taskRepository) {
        this.visitaRepository = visitaRepository;
        this.pdvRepository = pdvRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    // Inicia uma nova visita para um PDV
    public Visita iniciarVisita(Long pdvId) {

        // Busca o PDV pelo id
        // Se não existir -> lança erro
        Pdv pdv = pdvRepository.findById(pdvId)
                .orElseThrow(() -> new RuntimeException("PDV não encontrado."));

        // Busca a última visita desse PDV para mostrar histórico
        String historico = visitaRepository
                .findFirstByPdvIdOrderByDataDesc(pdvId)
                .map(Visita::getObservacao)
                .orElse("Sem anotações anteriores.");

        // Cria nova visita
        Visita visita = new Visita();
        visita.setData(LocalDate.now()); // Data de hoje
        visita.setPdv(pdv);              // Liga a visita ao PDV
        visita.setFinalizada(false);     // Ainda em andamento

        // Coloca histórico dentro da observação (caderno digital)
        visita.setObservacao("HISTÓRICO: " + historico);

        // Salva no banco e retorna
        return visitaRepository.save(visita);
    }

    /**
     * Finaliza a visita (encerra atendimento)
     */
    @Transactional
    public Visita finalizarVisita(Long visitaId, String anotacao) {

        // Busca visita existente
        Visita visita = buscarPorId(visitaId);

        // Atualiza observação com anotação final
        visita.setObservacao(anotacao);

        // Marca como finalizada
        visita.setFinalizada(true);

        // Salva no banco
        return visitaRepository.save(visita);
    }

    /**
     * Dashboard geral do dia (quantidade feita hoje)
     */
    public DashboardDiaDTO obterDashboardGeral() {

        LocalDate hoje = LocalDate.now();

        // Conta tarefas por tipo no dia
        long missoes = taskRepository.countByTipoAndVisitaData(TipoTask.MISSAO, hoje);
        long tasks = taskRepository.countByTipoAndVisitaData(TipoTask.TASK, hoje);
        long ofertas = taskRepository.countByTipoAndVisitaData(TipoTask.OFERTA, hoje);

        // Conta quantos PDVs foram finalizados hoje
        long pdvs = visitaRepository.countByDataAndFinalizadaTrue(hoje);

        // Retorna DTO para a tela
        return new DashboardDiaDTO(missoes, tasks, ofertas, pdvs);
    }

    // Busca visita por id (metodo reutilizado)
    public Visita buscarPorId(Long id) {
        return visitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visita não encontrada."));
    }

    // Lista todas as visitas
    public List<Visita> listarTodas() {
        return visitaRepository.findAll();
    }

    /**
     * Relatório de uma visita específica
     */
    public VisitaRelatorioDTO obterResumo(Long visitaId) {

        // Busca visita para pegar observação final
        Visita v = buscarPorId(visitaId);

        // Conta tarefas por tipo dentro da visita
        long missoes = taskRepository.countByVisitaIdAndTipo(visitaId, TipoTask.MISSAO);
        long tasks = taskRepository.countByVisitaIdAndTipo(visitaId, TipoTask.TASK);
        long ofertas = taskRepository.countByVisitaIdAndTipo(visitaId, TipoTask.OFERTA);

        // Retorna DTO com resumo
        return new VisitaRelatorioDTO(
                missoes,
                tasks,
                ofertas,
                v.getObservacao()
        );
    }
}

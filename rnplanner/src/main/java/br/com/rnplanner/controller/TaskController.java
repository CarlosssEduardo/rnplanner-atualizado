package br.com.rnplanner.controller;

import br.com.rnplanner.model.Task;
import br.com.rnplanner.model.Visita;
import br.com.rnplanner.repository.TaskRepository;
import br.com.rnplanner.repository.VisitaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Controller das tarefas
 * Tudo começa com /tasks
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    /*
     * Repository de task → salva e busca tarefas
     * Repository de visita → usado pra conectar task à visita
     */
    private final TaskRepository taskRepository;
    private final VisitaRepository visitaRepository;

    /*
     * Injeção de dependência
     */
    public TaskController(TaskRepository taskRepository,
                          VisitaRepository visitaRepository) {
        this.taskRepository = taskRepository;
        this.visitaRepository = visitaRepository;
    }

    /*
     * POST /tasks/visita/{visitaId}
     *
     * Cria uma task dentro de uma visita específica
     */
    @PostMapping("/visita/{visitaId}")
    public Task criarTask(@PathVariable Long visitaId,
                          @RequestBody Task task) {

        // Busca a visita no banco
        Visita visita = visitaRepository.findById(visitaId)
                .orElseThrow(() -> new RuntimeException("Visita não encontrada"));

        // Conecta a task à visita (FK visita_id)
        task.setVisita(visita);

        // Salva a task
        return taskRepository.save(task);
    }

    /*
     * PUT /tasks/{taskId}/concluir
     *
     * Marca a task como concluída
     */
    @PutMapping("/{taskId}/concluir")
    public Task concluirTask(@PathVariable Long taskId) {

        // Busca a task
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task não encontrada"));

        // Atualiza status
        task.setConcluida(true);

        return taskRepository.save(task);
    }

    /*
     * GET /tasks/visita/{visitaId}
     *
     * Lista todas as tasks daquela visita
     */
    @GetMapping("/visita/{visitaId}")
    public List<Task> listarPorVisita(@PathVariable Long visitaId) {
        return taskRepository.findByVisitaId(visitaId);
    }
}

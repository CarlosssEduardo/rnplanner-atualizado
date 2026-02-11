package br.com.rnplanner.controller;

import br.com.rnplanner.model.Task;
import br.com.rnplanner.model.Visita;
import br.com.rnplanner.repository.TaskRepository;
import br.com.rnplanner.repository.VisitaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final VisitaRepository visitaRepository;

    public TaskController(TaskRepository taskRepository,
                          VisitaRepository visitaRepository) {
        this.taskRepository = taskRepository;
        this.visitaRepository = visitaRepository;
    }

    // ✅ Criar task dentro da visita
    @PostMapping("/visita/{visitaId}")
    public Task criarTask(@PathVariable Long visitaId,
                          @RequestBody Task task) {

        Visita visita = visitaRepository.findById(visitaId)
                .orElseThrow(() -> new RuntimeException("Visita não encontrada"));

        task.setVisita(visita);

        return taskRepository.save(task);
    }

    // ✅ Marcar como concluída
    @PutMapping("/{taskId}/concluir")
    public Task concluirTask(@PathVariable Long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task não encontrada"));

        task.setConcluida(true);

        return taskRepository.save(task);
    }

    // ✅ Listar tasks da visita
    @GetMapping("/visita/{visitaId}")
    public List<Task> listarPorVisita(@PathVariable Long visitaId) {
        return taskRepository.findByVisitaId(visitaId);
    }
}

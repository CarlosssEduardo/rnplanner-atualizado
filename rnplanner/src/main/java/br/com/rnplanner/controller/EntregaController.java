package br.com.rnplanner.controller;

import br.com.rnplanner.model.Entrega;
import br.com.rnplanner.repository.EntregaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.Instant;
import java.time.ZoneId;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/entregas")
@CrossOrigin(origins = "*")
public class EntregaController {

    private final EntregaRepository entregaRepository;

    public EntregaController(EntregaRepository entregaRepository) {
        this.entregaRepository = entregaRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            entregaRepository.deleteAll(); // Limpa a rota anterior
            List<Entrega> entregas = new ArrayList<>();
            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) { primeiraLinha = false; continue; }

                String[] colunas = linha.split(";");

                // Agora lemos até a coluna 21 (que é onde está o Finished At)
                if (colunas.length > 21) {
                    try {
                        Entrega entrega = new Entrega();
                        entrega.setMotorista(colunas[3].trim());
                        entrega.setPdvId(Long.parseLong(colunas[5].replaceAll("[^0-9]", "")));
                        entrega.setNomePdv(colunas[6].trim());
                        entrega.setStatus(colunas[8].trim().toUpperCase());

                        // Pegando a Ordem de Visita (Coluna 13)
                        if (!colunas[13].isEmpty()) {
                            entrega.setVisitOrder(Integer.parseInt(colunas[13].trim()));
                        }

                        // Tratando as datas vindas do CSV
                        entrega.setDataRota(parseLocalDate(colunas[1]));
                        entrega.setDriverNotificationTime(parseLocalDateTime(colunas[19]));
                        entrega.setArrivedAt(parseLocalDateTime(colunas[20]));
                        entrega.setFinishedAt(parseLocalDateTime(colunas[21]));

                        entregas.add(entrega);
                    } catch (Exception e) {
                        // Ignora falhas de leitura de linhas vazias
                    }
                }
            }
            entregaRepository.saveAll(entregas);
            return ResponseEntity.ok("Radar atualizado! 🚚 " + entregas.size() + " rotas processadas.");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    // 🔥 A PORTA CERTA PARA O CELULAR CHAMAR (AGORA COM O RELÓGIO!):
    @GetMapping("/rastreio/{pdvId}")
    public ResponseEntity<Map<String, Object>> buscarEntrega(@PathVariable Long pdvId) {
        return entregaRepository.findFirstByPdvId(pdvId).map(entrega -> {

            Map<String, Object> response = new HashMap<>();
            response.put("pdvId", entrega.getPdvId());
            response.put("nomePdv", entrega.getNomePdv());
            response.put("motorista", entrega.getMotorista());
            response.put("status", entrega.getStatus());

            // 🕒 LÓGICA DO RELÓGIO:
            String horarioTexto = "Em rota de entrega";

            if ("CONCLUDED".equals(entrega.getStatus()) && entrega.getArrivedAt() != null) {
                // Se já entregou, mostra a hora real cravada que o caminhão chegou!
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                horarioTexto = "Entregue às " + entrega.getArrivedAt().format(formatter);
            } else if ("RESCHEDULED".equals(entrega.getStatus())) {
                horarioTexto = "Carga Adiada";
            } else if (entrega.getVisitOrder() != null && entrega.getVisitOrder() > 0) {
                // Se estiver pendente, mostra qual é a posição do cliente na fila do caminhão!
                horarioTexto = "Parada Nº " + entrega.getVisitOrder() + " da Rota";
            }

            response.put("horario", horarioTexto); // Envia o horário pro Front!

            return ResponseEntity.ok(response);

        }).orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // Ferramentas Táticas de Limpeza de Data
    // ==========================================
    private LocalDate parseLocalDate(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        try { return LocalDate.parse(str.trim()); } catch (Exception e) { return null; }
    }

    private LocalDateTime parseLocalDateTime(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        try {
            // 🔥 O SEGREDO DO FUSO HORÁRIO AQUI:
            // O CSV da logística manda a data com "Z" no final (UTC/Londres).
            if (str.trim().endsWith("Z")) {
                Instant instanteUtc = Instant.parse(str.trim());
                // Converte magicamente para o fuso de Brasília (-3 horas)
                return LocalDateTime.ofInstant(instanteUtc, ZoneId.of("America/Sao_Paulo"));
            }

            // Plano B (Se o CSV vier sem o Z, a gente recorta e aceita)
            if (str.length() >= 19) {
                return LocalDateTime.parse(str.substring(0, 19));
            }
            return LocalDateTime.parse(str.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
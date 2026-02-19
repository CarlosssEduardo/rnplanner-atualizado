package br.com.rnplanner.service;

import br.com.rnplanner.model.Pdv;
import br.com.rnplanner.repository.PdvRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



@Service
// Marca como classe de regra de negócio do Spring (service da aplicação)
@Slf4j
// Cria um logger profissional -> permite usar log.info, log.error etc
@RequiredArgsConstructor
// Lombok cria automaticamente o construtor com o repository (injeção de dependência)
public class PdvService {

    private final PdvRepository pdvRepository;
    // Repositório que conversa com o banco (salvar, deletar, buscar PDV)

    @Transactional
    // Garante que a importação seja atômica:
    // se der erro no meio -> nada é salvo
    public void importarExcel(MultipartFile file) throws Exception {

        List<Pdv> todosPdvs = new ArrayList<>();
        // Lista temporária para armazenar todos os PDVs antes de salvar no banco

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            // Abre o arquivo Excel enviado pelo usuário

            // Percorre TODAS as abas do Excel (cada aba = um dia da semana)
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {

                Sheet sheet = workbook.getSheetAt(i);
                String diaSemana = sheet.getSheetName();
                // O nome da aba vira o dia da rota (Segunda, Terça, etc)

                log.info("Processando aba: {}", diaSemana);
                // Log profissional mostrando qual aba está sendo processada

                for (Row row : sheet) {

                    // Pula a primeira linha (cabeçalho)
                    if (row.getRowNum() == 0) continue;

                    // Processa a linha e transforma em objeto PDV
                    Pdv pdv = processarLinha(row, diaSemana);

                    if (pdv != null) {
                        todosPdvs.add(pdv);
                        // Adiciona na lista para salvar depois em lote
                    }
                }
            }

            if (!todosPdvs.isEmpty()) {

                pdvRepository.deleteAll();
                // Limpa os PDVs antigos -> evita duplicação ao reimportar

                pdvRepository.saveAll(todosPdvs);
                // Salva todos de uma vez (mais desempenho)

                log.info("Sucesso! {} PDVs importados para toda a semana.", todosPdvs.size());
                // Log final de sucesso
            }
        }
    }

    // Metodo responsável por transformar UMA linha do Excel em um PDV
    private Pdv processarLinha(Row row, String diaSemana) {

        try {
            Cell cellId = row.getCell(0);   // Coluna A -> ID do PDV
            Cell cellNome = row.getCell(1); // Coluna B -> Nome do estabelecimento

            if (cellId == null || cellNome == null) return null;
            // Se faltar dado -> ignora linha

            Pdv pdv = new Pdv();
            // Cria objeto PDV em memória

            // Tratamento do ID:
            // Excel pode mandar número ou texto
            if (cellId.getCellType() == CellType.NUMERIC) {
                pdv.setId((long) cellId.getNumericCellValue());
            } else {
                pdv.setId(Long.parseLong(cellId.getStringCellValue().trim()));
            }

            pdv.setNome(cellNome.getStringCellValue().trim());
            // Define nome do PDV

            pdv.setDiaSemana(diaSemana);
            // Define o dia da rota baseado no nome da aba

            return pdv;

        } catch (Exception e) {

            log.error("Erro na linha {} da aba {}: {}",
                    row.getRowNum() + 1, diaSemana, e.getMessage());
            // Loga erro, mas continua importação

            return null;
        }
    }

    // Metodo usado pelo app para buscar a rota do dia
    public List<Pdv> listarPorDia(String dia) {
        return pdvRepository.findByDiaSemanaIgnoreCase(dia);
        // Busca no banco todos os PDVs daquele dia (sem diferenciar maiúsculo/minúsculo)
    }
}
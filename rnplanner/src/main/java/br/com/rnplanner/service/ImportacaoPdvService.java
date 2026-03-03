package br.com.rnplanner.service;

import br.com.rnplanner.model.Pdv;
import br.com.rnplanner.repository.PdvRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportacaoPdvService {

    private final PdvRepository pdvRepository;

    public ImportacaoPdvService(PdvRepository pdvRepository) {
        this.pdvRepository = pdvRepository;
    }

    @Transactional
    public void importar(InputStream inputStream) throws Exception {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0); // Pega a primeira aba

        List<Pdv> pdvsParaSalvar = new ArrayList<>();

        // Começa no '1' para pular o cabeçalho (SETOR, PDV, Nome)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue; // Pula linhas totalmente em branco

            try {
                // 👉 Lendo Coluna A (0): SETOR
                String setor = "";
                if (row.getCell(0) != null) {
                    // Força a leitura como texto, mesmo que seja o número "503"
                    row.getCell(0).setCellType(CellType.STRING);
                    setor = row.getCell(0).getStringCellValue().trim();
                }

                // 👉 Lendo Coluna B (1): CÓDIGO DO PDV
                Integer codigo = 0;
                if (row.getCell(1) != null) {
                    codigo = (int) row.getCell(1).getNumericCellValue();
                }

                // 👉 Lendo Coluna C (2): NOME DO PDV
                String nome = "";
                if (row.getCell(2) != null) {
                    row.getCell(2).setCellType(CellType.STRING);
                    nome = row.getCell(2).getStringCellValue().trim();
                }

                // Só salva se o código do PDV existir (evita salvar sujeira)
                if (codigo > 0) {
                    Pdv pdv = new Pdv();
                    pdv.setId((long) codigo); // O ID no banco será o próprio código
                    pdv.setCodigo(codigo);
                    pdv.setNome(nome);
                    pdv.setSetor(setor); // 👈 Etiqueta perfeita!

                    pdvsParaSalvar.add(pdv);
                }

            } catch (Exception e) {
                // Se der erro em uma linha (ex: formatada errado), avisa no console mas continua lendo o resto
                System.out.println("⚠️ Erro ao ler a linha " + (i + 1) + " do Excel: " + e.getMessage());
            }
        }

        // Salva o pelotão inteiro de uma vez no banco de dados!
        pdvRepository.saveAll(pdvsParaSalvar);
        workbook.close();
    }
}
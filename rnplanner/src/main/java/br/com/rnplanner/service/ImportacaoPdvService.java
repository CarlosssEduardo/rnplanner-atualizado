package br.com.rnplanner.service;

import br.com.rnplanner.model.Pdv;
import br.com.rnplanner.repository.PdvRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
// Marca como serviço do Spring.
// Ou seja, classe de regra de negócio que pode ser usada por controller.
public class ImportacaoPdvService {

    private final PdvRepository pdvRepository;
    // variável que guarda o repository.

    public ImportacaoPdvService(PdvRepository pdvRepository) {
        this.pdvRepository = pdvRepository;
        // Injeção de dependência:
        // Spring entrega o repository pronto pra usar.
    }

    public void importar(InputStream inputStream) throws Exception {
        // Metodo principal.
        // Recebe o arquivo Excel como entrada.

        Workbook workbook = WorkbookFactory.create(inputStream);
        // Abre o Excel usando Apache POI.

        Sheet sheet = workbook.getSheetAt(0);
        // Pega a primeira aba do Excel.

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            // Percorre todas as linhas.
            // Começa em 1 porque a linha 0 geralmente é o cabeçalho.

            Row row = sheet.getRow(i);
            // Pega a linha atual.

            Integer codigo = (int) row.getCell(0).getNumericCellValue();
            // Lê a primeira coluna (código).

            String nome = row.getCell(1).getStringCellValue();
            // Lê a segunda coluna (nome).

            Pdv pdv = new Pdv();
            // Cria um objeto PDV.

            pdv.setCodigo(codigo);
            pdv.setNome(nome);
            // Preenche os dados vindos do Excel.

            pdvRepository.save(pdv);
            // Salva no banco.
        }

        workbook.close();
        // Fecha o arquivo para liberar memória.
    }
}

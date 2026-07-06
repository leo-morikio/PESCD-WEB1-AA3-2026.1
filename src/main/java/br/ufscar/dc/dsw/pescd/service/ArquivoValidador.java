package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.exception.NegocioException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Validação de arquivos enviados por upload (S.02).
 */

@Component
public class ArquivoValidador {

    private static final String[] CONTENT_TYPES_ACEITOS = {
            "text/csv", "text/plain", "application/vnd.ms-excel", "application/csv", "application/octet-stream"
    };

    public void validarCsv(MultipartFile arquivo) throws IOException {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new NegocioException("Arquivo não enviado ou vazio.");
        }

        String nome = arquivo.getOriginalFilename();
        if (nome == null || !nome.toLowerCase().endsWith(".csv")) {
            throw new NegocioException("O arquivo deve ter extensão .csv.");
        }

        String contentType = arquivo.getContentType();
        if (contentType != null) {
            boolean permitido = false;
            for (int i = 0; i < CONTENT_TYPES_ACEITOS.length; i++) {
                if (CONTENT_TYPES_ACEITOS[i].equals(contentType)) {
                    permitido = true;
                }
            }
            if (!permitido) {
                throw new NegocioException("Tipo de arquivo não permitido: " + contentType);
            }
        }

        // Confere se o conteúdo parece texto (CSV real), e não um arquivo binário
        // apenas renomeado para .csv (ex: ZIP começa com 'PK', PDF começa com '%PDF').
        InputStream in = arquivo.getInputStream();
        byte[] cabecalho = new byte[8];
        int lidos = in.read(cabecalho);
        in.close();

        if (lidos > 0) {
            if (cabecalho[0] == 'P' && cabecalho[1] == 'K') {
                throw new NegocioException("O conteúdo do arquivo não corresponde a um CSV válido.");
            }
            if (cabecalho[0] == '%' && cabecalho[1] == 'P' && cabecalho[2] == 'D' && cabecalho[3] == 'F') {
                throw new NegocioException("O conteúdo do arquivo não corresponde a um CSV válido.");
            }
            for (int i = 0; i < lidos; i++) {
                if (cabecalho[i] == 0) {
                    throw new NegocioException("O conteúdo do arquivo não corresponde a um CSV válido (arquivo binário detectado).");
                }
            }
        }
    }
}

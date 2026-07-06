package br.ufscar.dc.dsw.pescd.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ArquivoStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String salvarPdf(MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new RuntimeException("O arquivo é obrigatório.");
        }


        String tipo = arquivo.getContentType();
        if (tipo == null || !tipo.equals("application/pdf")) {
            throw new RuntimeException("O arquivo deve ser um PDF.");
        }


        if (arquivo.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("O arquivo deve ter no máximo 5MB.");
        }

        try {

            Path pasta = Paths.get(uploadDir);
            Files.createDirectories(pasta);


            String nomeUnico = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
            Path destino = pasta.resolve(nomeUnico);


            Files.copy(arquivo.getInputStream(), destino);


            return destino.toString();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    // Lê os bytes de um arquivo já salvo, para servir como download (plano/documentação/relatório)
    public byte[] lerArquivo(String caminhoArquivo) {
        if (caminhoArquivo == null || caminhoArquivo.isBlank()) {
            throw new RuntimeException("Nenhum arquivo disponível.");
        }
        try {
            Path caminho = Paths.get(caminhoArquivo);
            return Files.readAllBytes(caminho);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}
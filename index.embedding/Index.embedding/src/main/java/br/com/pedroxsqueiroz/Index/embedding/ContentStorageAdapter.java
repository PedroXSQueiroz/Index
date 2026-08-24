package br.com.pedroxsqueiroz.Index.embedding;

import br.com.pedroxsqueiroz.Index.concept.ports.repo.ContentStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Armazena o texto bruto de cada Content em arquivos locais.
 * Em produção, substitua por um adapter de object storage (S3, GCS, etc.).
 */
@Service
public class ContentStorageAdapter implements ContentStoragePort {

    private final Path storageDir;

    public ContentStorageAdapter(
            @Value("${index.content.storage.dir:./content-storage}") String storageDirPath) {
        this.storageDir = Path.of(storageDirPath);
        try {
            Files.createDirectories(storageDir);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível criar o diretório de storage: " + storageDirPath, e);
        }
    }

    @Override
    public String save(String content, String storageId, int page) {
        try {
            String fileName = String.format("%s__%d", storageId, page);
            Files.writeString(storageDir.resolve(fileName), content);
            return storageId;
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao salvar conteúdo com id " + storageId, e);
        }
    }

    @Override
    public String getContent(String storageId, int page, int offset, int end) {
        try {
            String fileName = String.format("%s__%d", storageId, page);
            String fullContent = Files.readString(storageDir.resolve(fileName));
            int safeEnd = Math.min(end, fullContent.length());
            return fullContent.substring(offset, safeEnd);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao ler conteúdo com storageId " + storageId, e);
        }
    }
}

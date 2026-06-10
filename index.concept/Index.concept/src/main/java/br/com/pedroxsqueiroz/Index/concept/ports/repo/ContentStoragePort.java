package br.com.pedroxsqueiroz.Index.concept.ports.repo;

import br.com.pedroxsqueiroz.Index.concept.models.Content;
import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;

import java.util.List;

public interface ContentStoragePort {

    /**
     * Persiste o texto em um sistema de armazenamento externo (ex: filesystem, object storage)
     * e retorna o identificador de localização (storageId).
     */
    String save(String content, String id, int page);

    /**
     * Recupera a fatia [{@code offset}, {@code limit}] do conteúdo identificado por {@code storageId}.
     */
    String getContent(String storageId, int page, int offset, int limit);
}

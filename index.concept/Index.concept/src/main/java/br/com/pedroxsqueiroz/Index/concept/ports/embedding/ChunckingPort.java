package br.com.pedroxsqueiroz.Index.concept.ports.embedding;

import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;

import java.util.List;

public interface ChunckingPort {

    /**
     * Segmenta {@code content} em trechos semânticos e retorna os limites de cada trecho.
     * Os campos {@code start} e {@code end} de cada {@link ContentChunck} indicam os índices
     * de caracteres no texto original. O campo {@code content} é deixado em branco —
     * o vínculo com a entidade {@link br.com.pedroxsqueiroz.Index.concept.models.Content}
     * é responsabilidade do chamador após persistir o conteúdo.
     */
    List<ContentChunck> buildChuncks(String content);
}

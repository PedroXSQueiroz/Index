package br.com.pedroxsqueiroz.Index.concept.ports.embedding;

import br.com.pedroxsqueiroz.Index.concept.models.Embedding;

public interface EmbeddingFactoryPort {

    Embedding generate(String text);
}

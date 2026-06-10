package br.com.pedroxsqueiroz.Index.concept.services;

import br.com.pedroxsqueiroz.Index.concept.models.Embedding;
import br.com.pedroxsqueiroz.Index.concept.ports.embedding.EmbeddingFactoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    private final EmbeddingFactoryPort embeddingFactory;

    public EmbeddingService(@Autowired EmbeddingFactoryPort embeddingFactoryPort) {
        this.embeddingFactory = embeddingFactoryPort;
    }

    public Embedding generateEmbedding(String content) {
        return embeddingFactory.generate(content);
    }
}

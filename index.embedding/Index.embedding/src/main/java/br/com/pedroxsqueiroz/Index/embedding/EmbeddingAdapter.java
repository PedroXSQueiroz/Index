package br.com.pedroxsqueiroz.Index.embedding;

import br.com.pedroxsqueiroz.Index.concept.models.Embedding;
import br.com.pedroxsqueiroz.Index.concept.ports.embedding.EmbeddingFactoryPort;
import br.com.pedroxsqueiroz.Index.embedding.dto.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmbeddingAdapter implements EmbeddingFactoryPort {

    private final WebClient webClient;

    public EmbeddingAdapter(
            @Value("${index.embedding.service.url:http://localhost:5000}") String serviceUrl) {
        this.webClient = WebClient.builder().baseUrl(serviceUrl).build();
    }

    @Override
    public Embedding generate(String text) {
        EmbeddingResponse response = webClient.post()
                .uri("/embed")
                .bodyValue(Map.of("text", text))
                .retrieve()
                .bodyToMono(EmbeddingResponse.class)
                .block();
        return Embedding.of(response.getEmbedding());
    }
}

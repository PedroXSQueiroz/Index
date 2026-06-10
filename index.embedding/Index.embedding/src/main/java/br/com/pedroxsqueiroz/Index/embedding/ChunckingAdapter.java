package br.com.pedroxsqueiroz.Index.embedding;

import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;
import br.com.pedroxsqueiroz.Index.concept.ports.embedding.ChunckingPort;
import br.com.pedroxsqueiroz.Index.embedding.dto.ChunkResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class ChunckingAdapter implements ChunckingPort {

    private final WebClient webClient;

    public ChunckingAdapter(
            @Value("${index.embedding.service.url:http://localhost:5000}") String serviceUrl) {
        this.webClient = WebClient.builder().baseUrl(serviceUrl).build();
    }

    @Override
    public List<ContentChunck> buildChuncks(String content) {
        List<ChunkResponse> response = webClient.post()
                .uri("/chunks")
                .bodyValue(Map.of("text", content))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ChunkResponse>>() {})
                .block();

        return response.stream()
                .map(chunk -> ContentChunck.builder()
                        .start(chunk.getOffset())
                        .end(chunk.getLimit())
                        .build())
                .toList();
    }
}

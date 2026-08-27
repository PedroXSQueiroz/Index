package br.com.pedroxsqueiroz.Index.embedding;

import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;
import br.com.pedroxsqueiroz.Index.concept.ports.concept.ConceptHelperPort;
import br.com.pedroxsqueiroz.Index.concept.ports.repo.ContentStoragePort;
import br.com.pedroxsqueiroz.Index.embedding.dto.ResumeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class ConceptHelperAdapter implements ConceptHelperPort {

    private final WebClient          webClient;
    private final ContentStoragePort contentStoragePort;

    private static final String NAME_LANGUAGE = "pt";

    private final int   descriptionMinLength;
    private final int   descriptionMaxLength;
    private final float descriptionLengthPenalty;

    private final int nameMinLength;
    private final int nameMaxLength;

    public ConceptHelperAdapter(
        ContentStoragePort contentStoragePort,
        @Value("${index.embedding.service.url:http://localhost:5000}") String serviceUrl,
        @Value("${index.concept.description.min-length:80}")      int   descriptionMinLength,
        @Value("${index.concept.description.max-length:256}")     int   descriptionMaxLength,
        @Value("${index.concept.description.length-penalty:1.0}") float descriptionLengthPenalty,
        @Value("${index.concept.name.min-length:8}")              int   nameMinLength,
        @Value("${index.concept.name.max-length:32}")             int   nameMaxLength
    ) {
        this.webClient                 = WebClient.builder().baseUrl(serviceUrl).build();
        this.contentStoragePort        = contentStoragePort;
        this.descriptionMinLength      = descriptionMinLength;
        this.descriptionMaxLength      = descriptionMaxLength;
        this.descriptionLengthPenalty  = descriptionLengthPenalty;
        this.nameMinLength             = nameMinLength;
        this.nameMaxLength             = nameMaxLength;
    }

    @Override
    public String generateConceptDescription(List<ContentChunck> chuncks) {
        ResumeResponse response = webClient.post()
                .uri("/resume")
                .bodyValue(Map.of(
                        "texts",          texts(chuncks),
                        "min_length",     descriptionMinLength,
                        "max_length",     descriptionMaxLength,
                        "length_penalty", descriptionLengthPenalty
                ))
                .retrieve()
                .bodyToMono(ResumeResponse.class)
                .block();

        return response != null ? response.getResume() : "";
    }

    @Override
    public String generateConceptName(List<ContentChunck> similarChuncks) {
        ResumeResponse response = webClient.post()
                .uri("/title")
                .bodyValue(Map.of(
                        "texts",      texts(similarChuncks),
                        "min_length", nameMinLength,
                        "max_length", nameMaxLength,
                        "language",   NAME_LANGUAGE
                ))
                .retrieve()
                .bodyToMono(ResumeResponse.class)
                .block();

        return response != null ? response.getResume() : "";
    }

    private List<String> texts(List<ContentChunck> chuncks) {
        return chuncks.stream()
                .map(c -> contentStoragePort.getContent(
                        c.getContent().getStorageId(),
                        c.getPage(),
                        c.getStart(),
                        c.getEnd()
                ))
                .toList();
    }
}

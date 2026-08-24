package br.com.pedroxsqueiroz.Index.concept.services;

import br.com.pedroxsqueiroz.Index.concept.constants.ContentType;
import br.com.pedroxsqueiroz.Index.concept.exceptions.InconsistentContentRequestException;
import br.com.pedroxsqueiroz.Index.concept.models.Concept;
import br.com.pedroxsqueiroz.Index.concept.models.Content;
import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;
import br.com.pedroxsqueiroz.Index.concept.models.Embedding;
import br.com.pedroxsqueiroz.Index.concept.ports.embedding.ChunckingPort;
import br.com.pedroxsqueiroz.Index.concept.ports.repo.ContentRepositoryPort;
import br.com.pedroxsqueiroz.Index.concept.ports.repo.ContentStoragePort;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ContentService {

    private ContentRepositoryPort contentRepository;
    private ContentStoragePort contentStorage;
    private ChunckingPort chuncking;
    private EmbeddingService embedding;

    private ConceptService conceptService;

    private int maxChuncksBatch;

    public ContentService(
            @Autowired ContentRepositoryPort contentRepositoryPort,
            @Autowired ContentStoragePort contentStoragePort,
            @Autowired ChunckingPort chunckingPort,
            @Autowired EmbeddingService embedding,
            @Autowired ConceptService conceptService,
            @Value("${index.embedding.maxEmbeddingGenBatch}") int maxChuncksBatch) {

        this.contentRepository = contentRepositoryPort;
        this.contentStorage = contentStoragePort;
        this.chuncking = chunckingPort;
        this.embedding = embedding;
        this.maxChuncksBatch = maxChuncksBatch;
        this.conceptService = conceptService;

    }

    public Content saveContent(List<String> pages, String name, String description, String author, ContentType type)
    {
        //List<ContentChunck> chuncks = new ArrayList<>();
        UUID storageId = UUID.randomUUID();
        Content savedContent = contentRepository.save(
                Content.builder()
                        .name(name)
                        .description(description)
                        .storageId(storageId.toString())
                        .author(author)
                        .type(type)
                        .uploadDate(new Date())
                        .build()
        );

        for(int pageCounter = 0; pageCounter < pages.size(); pageCounter++)
        {
            final int currentPageIndex = pageCounter;
            String currentPage = pages.get(pageCounter);

            contentStorage.save(currentPage, storageId.toString(), currentPageIndex);

            chuncking.buildChuncks(currentPage)
                .forEach(chunck -> {
                    chunck.setPage(currentPageIndex);
                    chunck.setContent(savedContent);
                    contentRepository.saveChunck(chunck);
                });

        }

        return savedContent;
    }

    public String getChunckContent(List<ContentChunck> chuncks) throws InconsistentContentRequestException {

        if(Objects.isNull(chuncks) || chuncks.isEmpty())
        {
            throw new InconsistentContentRequestException();
        }

        String contentId = chuncks.getFirst().getContent().getId();

        StringBuilder contentFromChuncks = new StringBuilder();

        for(ContentChunck currentChunck : chuncks){

            if(!currentChunck.getContent().getId().equals(contentId))
            {
                throw new InconsistentContentRequestException();
            }

            contentFromChuncks.append(
                contentStorage.getContent(
                    currentChunck.getContent().getStorageId(),
                    currentChunck.getPage(),
                    currentChunck.getStart(),
                    currentChunck.getEnd()
                )
            );
        }

        return contentFromChuncks.toString();
    };

    public String getChunckContent(List<String> chunckIds, String contentId)
    {
        return "";
    }

    public @Nullable List<Content> list(int offset, int limit) {
        return contentRepository.findAll(offset, limit);
    }

    public List<ContentChunck> getContentChunck(Content savedContent, int startPage, int endPage) {
        return contentRepository.findContentChunckOfContentBetweenPages(savedContent, startPage, endPage);
    }

    public Content getContent(String contentId) {
        return contentRepository.get(contentId);
    }

    public void generateEmbeddingsOf(Content content) {

        List<ContentChunck> currentChunckSet;
        int offset = 0;

        do {
            currentChunckSet = contentRepository.findContentChunckOfContent(content, offset, maxChuncksBatch);

            for (ContentChunck currentChunck : currentChunckSet) {

                try {
                    //FIXME: THIS EXCEPTION SHOULD BE ON THE SIGNATURE?
                    Embedding currentEmbedding = this.embedding.generateEmbedding(getChunckContent(List.of( currentChunck )));
                    currentChunck.setEmbedding(currentEmbedding);
                    contentRepository.saveChunck(currentChunck);

                    //TODO: THE RELATIONSHIP WITH CONCEPT SHOULD BE MADE HERE?
                    List<Concept> correspondingConcepts = conceptService.findConcepts(currentEmbedding);

                    correspondingConcepts.forEach(concept -> {
                        concept.getChuncks().add(currentChunck);
                        conceptService.update(concept);
                    });
                } catch (InconsistentContentRequestException e) {
                    throw new RuntimeException(e);
                }

            }

            offset += maxChuncksBatch;

        } while (currentChunckSet.size() == maxChuncksBatch);

    }

    public String getPage(Content content, int page) {
        return null;
    }

}

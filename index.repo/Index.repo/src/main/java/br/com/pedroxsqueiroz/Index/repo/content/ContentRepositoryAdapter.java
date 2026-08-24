package br.com.pedroxsqueiroz.Index.repo.content;

import br.com.pedroxsqueiroz.Index.concept.models.Concept;
import br.com.pedroxsqueiroz.Index.concept.models.Content;
import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;
import br.com.pedroxsqueiroz.Index.concept.models.Embedding;
import br.com.pedroxsqueiroz.Index.concept.ports.repo.ContentRepositoryPort;
import br.com.pedroxsqueiroz.Index.repo.concept.ConceptEmbeddingJpaRepository;
import br.com.pedroxsqueiroz.Index.repo.concept.ConceptMapper;
import br.com.pedroxsqueiroz.Index.repo.content.entity.ContentChunckEntity;
import br.com.pedroxsqueiroz.Index.repo.content.entity.ContentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContentRepositoryAdapter implements ContentRepositoryPort {

    private final ContentJpaRepository contentRepo;
    private final ContentChunckJpaRepository chunkRepo;
    private final ConceptEmbeddingJpaRepository embeddingRepo;
    private final float minTolerance;

    public ContentRepositoryAdapter(@Autowired ContentJpaRepository contentRepo,
                                    @Autowired ContentChunckJpaRepository chunkRepo,
                                    @Autowired ConceptEmbeddingJpaRepository embeddingRepo,
                                    @Value("${MIN_TOLLERANCE:0.75}") float minTolerance) {
        this.contentRepo = contentRepo;
        this.chunkRepo = chunkRepo;
        this.embeddingRepo = embeddingRepo;
        this.minTolerance = minTolerance;
    }

    @Override
    public Content save(Content content) {
        ContentEntity entity = ContentMapper.toEntity(content);

        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
            entity.setStorageId(entity.getStorageId());
            /*
            entity = ContentEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .storageId(entity.getStorageId())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .build();
             */
        }
        return ContentMapper.toDomain(contentRepo.save(entity));
    }

    @Override
    public ContentChunck saveChunck(ContentChunck chunck) {
        ContentEntity contentRef = contentRepo.getReferenceById(chunck.getContent().getId());
        ContentChunckEntity entity = ContentChunckEntity.builder()
                .id(chunck.getId() != null ? chunck.getId() : UUID.randomUUID().toString())
                .content(contentRef)
                .startPos(chunck.getStart())
                .endPos(chunck.getEnd())
                .build();

        ContentChunckEntity chunckSaved = chunkRepo.save(entity);
        chunck.setId(chunckSaved.getId());

        Embedding embedding = chunck.getEmbedding();
        if(embedding != null) {
            embeddingRepo.save(
                ConceptMapper.embeddingToEntity(embedding, chunckSaved)
            );
        }

        return chunck;
    }

    @Override
    public List<Content> findAll(int offset, int limit) {
        return contentRepo.findAll(PageRequest.of(offset, limit))
                .stream()
                .map( contentEntity -> {
                    Content content = ContentMapper.toDomain(contentEntity);
                    content.setPagesCount(chunkRepo.countAllByContent(contentEntity));
                    return content;
                })
                .toList();
    }

    @Override
    public List<ContentChunck> findContentChunckOfContent(Content content, int offset, int limit) {
        return chunkRepo.findAllByContent(ContentMapper.toEntity(content), PageRequest.of(offset, limit))
                .stream()
                .map(ContentMapper::chunkToDomain)
                .toList();
    }

    @Override
    public List<ContentChunck> findContentChunckOfContentBetweenPages(Content content, int startPage, int endPage) {
        return chunkRepo.findAllByContentAndPageBetween(ContentMapper.toEntity(content), startPage, endPage )
                .stream()
                .map(ContentMapper::chunkToDomain)
                .toList();
    }

    @Override
    public Set<ContentChunck> findSimilarContentChuncks(Embedding embedding) {
        String vectorStr = embedding.getVector().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));

        return embeddingRepo.findSimilar(vectorStr, minTolerance)
                .stream()
                .map(embeddingEntity -> {
                    ContentChunck chunk = ContentMapper.chunkToDomain(embeddingEntity.getChunk());
                    chunk.setEmbedding(ConceptMapper.embeddingToDomain(embeddingEntity));
                    return chunk;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public Content get(String contentId) {
        Optional<ContentEntity> contentEntityFound = contentRepo.findById(contentId);

        if(!contentEntityFound.isPresent()) {
            return null;
        }

        Content content = ContentMapper.toDomain(
                contentEntityFound.get()
        );

        content.setPagesCount(chunkRepo.countAllByContent(contentEntityFound.get()));

        return content;
    }

    @Override
    public String getContentChunck(String contentId, int offset, int limit) {
        // A leitura do texto bruto é delegada ao ContentStoragePort (filesystem).
        // Este método retorna apenas os metadados do trecho — o texto em si
        // deve ser obtido via ContentStoragePort.getContent(storageId, offset, limit).
        throw new UnsupportedOperationException(
                "Use ContentStoragePort.getContent(storageId, offset, limit) para ler o texto bruto.");
    }

    @Override
    public String getContentChunck(ContentChunck contentChunck) {
        return getContentChunck(contentChunck.getContent().getId(),
                contentChunck.getStart(), contentChunck.getEnd());
    }

    @Override
    public List<Concept> getRelatedConcepts(Content content) {
        // TODO: implementar busca de conceitos vinculados a um Content
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Content> containsConcepts(List<Concept> concepts) {
        // TODO: implementar busca de Contents que referenciam os conceitos informados
        throw new UnsupportedOperationException();
    }

    @Override
    public Concept relateConcpet(List<ContentChunck> contentChunck, Concept concept) {
        // TODO: implementar vínculo entre trechos e conceito
        throw new UnsupportedOperationException();
    }
}

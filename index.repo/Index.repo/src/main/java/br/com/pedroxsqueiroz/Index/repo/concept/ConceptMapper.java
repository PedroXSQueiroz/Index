package br.com.pedroxsqueiroz.Index.repo.concept;

import br.com.pedroxsqueiroz.Index.concept.models.Concept;
import br.com.pedroxsqueiroz.Index.concept.models.ConceptRelation;
import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;
import br.com.pedroxsqueiroz.Index.concept.models.Embedding;
import br.com.pedroxsqueiroz.Index.repo.concept.entity.ConceptEmbeddingEntity;
import br.com.pedroxsqueiroz.Index.repo.concept.entity.ConceptEntity;
import br.com.pedroxsqueiroz.Index.repo.content.ContentMapper;
import br.com.pedroxsqueiroz.Index.repo.content.entity.ContentChunckEntity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConceptMapper {

    public static Concept toDomain(ConceptEntity entity) {
        if (entity == null) return null;

        // Navega concept → chuncks → embedding (hierarquia correta do domínio)
        Set<ContentChunck> chuncks = null;
        if (entity.getChuncks() != null) {
            chuncks = entity.getChuncks().stream()
                    .map(chunkEntity -> {
                        ContentChunck chunk = ContentMapper.chunkToDomain(chunkEntity);
                        chunk.setEmbedding(embeddingToDomain(chunkEntity.getEmbedding()));
                        return chunk;
                    })
                    .collect(Collectors.toSet());
        }

        Set<ConceptRelation> relations = null;
        if (entity.getRelatedConcepts() != null) {
            relations = entity.getRelatedConcepts().stream()
                    .map(r ->
                        new ConceptRelation(
                            toDomain(r.getOrigin()),
                            r.getType(),
                            toDomain(r.getTarget())
                        )
                    ).collect(Collectors.toSet());
        }

        return Concept.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .chuncks(chuncks)
                .relatedConcepts(relations)
                .build();
    }

    public static ConceptEntity toEntity(Concept concept) {
        return ConceptEntity.builder()
                .id(concept.getId())
                .name(concept.getName())
                .description(concept.getDescription())
                .chuncks(
                    concept.getChuncks()
                        .stream()
                        .map( ContentMapper::chunkToEntity )
                        .toList()
                ).build();
    }

    public static Embedding embeddingToDomain(ConceptEmbeddingEntity entity) {
        if (entity == null || entity.getEmbedding() == null) return null;
        return Embedding.of(entity.getEmbedding());
    }

    public static ConceptEmbeddingEntity embeddingToEntity(Embedding embedding, ContentChunckEntity chunkEntity) {
        if (embedding == null) return null;
        return ConceptEmbeddingEntity.builder()
                .chunk(chunkEntity)
                .embedding(embedding.getVector())
                .build();
    }
}

package br.com.pedroxsqueiroz.Index.repo.concept;

import br.com.pedroxsqueiroz.Index.concept.constants.ConceptRelationTypesEnum;
import br.com.pedroxsqueiroz.Index.concept.models.Concept;
import br.com.pedroxsqueiroz.Index.concept.models.ConceptRelation;
import br.com.pedroxsqueiroz.Index.concept.models.Content;
import br.com.pedroxsqueiroz.Index.concept.models.Embedding;
import br.com.pedroxsqueiroz.Index.concept.ports.repo.ConceptRepositoryPort;
import br.com.pedroxsqueiroz.Index.repo.concept.entity.ConceptEntity;
import br.com.pedroxsqueiroz.Index.repo.concept.entity.ConceptRelationEntity;
import br.com.pedroxsqueiroz.Index.repo.content.ContentChunckJpaRepository;
import br.com.pedroxsqueiroz.Index.repo.content.ContentJpaRepository;
import br.com.pedroxsqueiroz.Index.repo.content.ContentMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConceptRepositoryAdapter implements ConceptRepositoryPort {

    private final ConceptJpaRepository conceptRepo;
    private final ConceptRelationJpaRepository relationRepo;
    private final ContentJpaRepository contentRepo;
    private final ContentChunckJpaRepository chunckRepo;

    public ConceptRepositoryAdapter(ConceptJpaRepository conceptRepo,
                                    ConceptRelationJpaRepository relationRepo,
                                    ContentJpaRepository contentRepo,
                                    ContentChunckJpaRepository chunckRepo) {
        this.conceptRepo = conceptRepo;
        this.relationRepo = relationRepo;
        this.contentRepo = contentRepo;
        this.chunckRepo = chunckRepo;
    }

    @Override
    public Optional<Concept> findByName(String name) {
        return conceptRepo.findByName(name).map(ConceptMapper::toDomain);
    }

    @Override
    public List<Concept> findByRelatedConceptNames(List<String> names) {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Embedding> listAllConceptVectors() {
        return new HashMap<>();
    }

    @Override
    public List<Concept> find(Embedding queryVector, float tolerance) {
        String vectorStr = queryVector.getVector().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));

        return conceptRepo.findBySimilarity(vectorStr, tolerance).stream()
                .map(ConceptMapper::toDomain)
                .toList();
    }

    @Override
    public Concept save(Concept concept) {
        ConceptEntity entity = ConceptMapper.toEntity(concept);
        entity.setId(concept.getId() != null ? concept.getId() : UUID.randomUUID().toString());
        ConceptEntity conceptEntitySaved = conceptRepo.save(entity);

        concept.getChuncks().stream()
                .map(ContentMapper::chunkToEntity)
                .forEach( chunck -> {
                    chunck.setConcept(conceptEntitySaved);
                    chunckRepo.save(chunck);
                });

        return ConceptMapper.toDomain(conceptEntitySaved);
    }

    @Override
    public ConceptRelation saveConceptRelation(Concept origin, ConceptRelationTypesEnum type, Concept target) {
        ConceptEntity originEntity = conceptRepo.getReferenceById(origin.getId());
        ConceptEntity targetEntity = conceptRepo.getReferenceById(target.getId());

        ConceptRelationEntity relation = ConceptRelationEntity.builder()
                .origin(originEntity)
                .type(type)
                .target(targetEntity)
                .build();

        relationRepo.save(relation);
        return new ConceptRelation(origin, type, target);
    }

    @Override
    public List<Concept> list(Integer offset, Integer limit) {
        return conceptRepo.findAll(PageRequest.of(offset, limit)).stream()
                .map(ConceptMapper::toDomain)
                .toList();
    }

    @Override
    public List<Concept> list(Content content, Integer offset, Integer limit) {
        return contentRepo.findById(content.getId())
                .map(contentEntity -> {
                            return conceptRepo.findAllByChuncks_Content(
                                    contentEntity,
                                    PageRequest.of(offset, limit)
                            ).stream()
                            .map(ConceptMapper::toDomain)
                            .toList();
                })
                .orElse(List.of());
    }
}

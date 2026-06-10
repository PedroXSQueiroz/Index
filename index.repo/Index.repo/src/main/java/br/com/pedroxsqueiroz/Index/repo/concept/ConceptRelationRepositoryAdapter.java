package br.com.pedroxsqueiroz.Index.repo.concept;

import br.com.pedroxsqueiroz.Index.concept.constants.ConceptRelationTypesEnum;
import br.com.pedroxsqueiroz.Index.concept.models.Concept;
import br.com.pedroxsqueiroz.Index.concept.models.ConceptRelation;
import br.com.pedroxsqueiroz.Index.concept.ports.repo.ConceptRelationRepositoryPort;
import br.com.pedroxsqueiroz.Index.repo.concept.entity.ConceptEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConceptRelationRepositoryAdapter implements ConceptRelationRepositoryPort {

    private final ConceptRelationJpaRepository relationRepo;
    private final ConceptJpaRepository conceptRepo;

    public ConceptRelationRepositoryAdapter(@Autowired ConceptRelationJpaRepository relationRepo,
                                            @Autowired ConceptJpaRepository conceptRepo) {
        this.relationRepo = relationRepo;
        this.conceptRepo = conceptRepo;
    }

    @Override
    public List<ConceptRelation> findByOrigin(Concept origin, ConceptRelationTypesEnum relationType) {
        ConceptEntity originEntity = conceptRepo.getReferenceById(origin.getId());
        return relationRepo.findByOriginAndType(originEntity, relationType).stream()
                .map(r -> new ConceptRelation(
                        ConceptMapper.toDomain(r.getOrigin()),
                        r.getType(),
                        ConceptMapper.toDomain(r.getTarget())
                ))
                .toList();
    }

    @Override
    public List<ConceptRelation> findByTarget(Concept target, ConceptRelationTypesEnum relationType) {
        ConceptEntity targetEntity = conceptRepo.getReferenceById(target.getId());
        return relationRepo.findByTargetAndType(targetEntity, relationType).stream()
                .map(r -> new ConceptRelation(
                        ConceptMapper.toDomain(r.getOrigin()),
                        r.getType(),
                        ConceptMapper.toDomain(r.getTarget())
                ))
                .toList();
    }

    @Override
    public List<ConceptRelation> findByOriginAndTarget(Concept origin, ConceptRelationTypesEnum relationType, Concept target) {
        ConceptEntity originEntity = conceptRepo.getReferenceById(origin.getId());
        ConceptEntity targetEntity = conceptRepo.getReferenceById(target.getId());
        return relationRepo.findByOriginAndType(originEntity, relationType).stream()
                .filter(r -> r.getTarget().getId().equals(targetEntity.getId()))
                .map(r -> new ConceptRelation(
                        ConceptMapper.toDomain(r.getOrigin()),
                        r.getType(),
                        ConceptMapper.toDomain(r.getTarget())
                ))
                .toList();
    }
}

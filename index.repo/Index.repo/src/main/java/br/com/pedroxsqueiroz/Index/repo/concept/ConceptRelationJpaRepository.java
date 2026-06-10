package br.com.pedroxsqueiroz.Index.repo.concept;

import br.com.pedroxsqueiroz.Index.concept.constants.ConceptRelationTypesEnum;
import br.com.pedroxsqueiroz.Index.repo.concept.entity.ConceptEntity;
import br.com.pedroxsqueiroz.Index.repo.concept.entity.ConceptRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConceptRelationJpaRepository extends JpaRepository<ConceptRelationEntity, String> {
    List<ConceptRelationEntity> findByOriginAndType(ConceptEntity origin, ConceptRelationTypesEnum type);
    List<ConceptRelationEntity> findByTargetAndType(ConceptEntity target, ConceptRelationTypesEnum type);
}

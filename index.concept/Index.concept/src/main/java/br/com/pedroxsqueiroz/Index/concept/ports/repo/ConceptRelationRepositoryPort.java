package br.com.pedroxsqueiroz.Index.concept.ports.repo;

import br.com.pedroxsqueiroz.Index.concept.constants.ConceptRelationTypesEnum;
import br.com.pedroxsqueiroz.Index.concept.models.Concept;
import br.com.pedroxsqueiroz.Index.concept.models.ConceptRelation;

import java.util.List;

public interface ConceptRelationRepositoryPort {

    List<ConceptRelation> findByOrigin(Concept origin, ConceptRelationTypesEnum relationType);

    List<ConceptRelation> findByTarget(Concept target, ConceptRelationTypesEnum relationType);

    List<ConceptRelation> findByOriginAndTarget(Concept origin, ConceptRelationTypesEnum relationType, Concept target);

}

package br.com.pedroxsqueiroz.Index.concept.ports.repo;

import br.com.pedroxsqueiroz.Index.concept.constants.ConceptRelationTypesEnum;
import br.com.pedroxsqueiroz.Index.concept.models.Concept;
import br.com.pedroxsqueiroz.Index.concept.models.ConceptRelation;
import br.com.pedroxsqueiroz.Index.concept.models.Content;
import br.com.pedroxsqueiroz.Index.concept.models.Embedding;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ConceptRepositoryPort {

    Optional<Concept> findByName(String name);

    List<Concept> findByRelatedConceptNames(List<String> names);

    Map<String, Embedding> listAllConceptVectors();

    List<Concept> find(Embedding vector, float tolerance);

    Concept save(Concept concept);

    ConceptRelation saveConceptRelation(Concept concept, ConceptRelationTypesEnum type, Concept target);

    List<Concept> list(Integer offset, Integer limit);

    List<Concept> list(Content content, Integer offset, Integer limit);
}

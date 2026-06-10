package br.com.pedroxsqueiroz.Index.concept.ports.concept;

import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;

import java.util.List;

public interface ConceptHelperPort {

    String generateConceptDescription(List<ContentChunck> chuncks);

    String generateConceptName(List<ContentChunck> similarChuncks);
}

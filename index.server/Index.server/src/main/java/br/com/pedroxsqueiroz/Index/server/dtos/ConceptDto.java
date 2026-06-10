package br.com.pedroxsqueiroz.Index.server.dtos;


import br.com.pedroxsqueiroz.Index.concept.models.Concept;
import lombok.Getter;

import java.util.List;

@Getter
public class ConceptDto {

    public ConceptDto(Concept concept) {
        this.id = concept.getId();
        this.name = concept.getName();
        this.description = concept.getDescription();
    }

    public ConceptDto(Concept concept, List<ContentChunckDto> chuncks) {
        this(concept);
        this.chuncks = chuncks;
    }

    private String id;
    private String name;
    private String description;
    private List<ContentChunckDto> chuncks;
}

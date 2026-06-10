package br.com.pedroxsqueiroz.Index.concept.models;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Concept {
    @EqualsAndHashCode.Include
    private String id;
    private String name;
    private String description;
    private Set<ContentChunck> chuncks;
    @ToString.Exclude
    private Set<ConceptRelation> relatedConcepts;
}

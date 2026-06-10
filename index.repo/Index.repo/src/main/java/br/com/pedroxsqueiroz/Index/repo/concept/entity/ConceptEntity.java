package br.com.pedroxsqueiroz.Index.repo.concept.entity;

import br.com.pedroxsqueiroz.Index.repo.content.entity.ContentChunckEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "concepts")
public class ConceptEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @Column(columnDefinition = "VARCHAR(255)")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Trechos de conteúdo que definem este conceito.
     * Cada chunk carrega seu próprio embedding de forma lazy (via 1:1 com concept_embeddings).
     */
    @OneToMany(mappedBy = "concept", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ContentChunckEntity> chuncks;

    @OneToMany(mappedBy = "origin", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ConceptRelationEntity> relatedConcepts;
}

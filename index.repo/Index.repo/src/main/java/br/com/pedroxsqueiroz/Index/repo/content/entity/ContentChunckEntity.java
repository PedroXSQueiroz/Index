package br.com.pedroxsqueiroz.Index.repo.content.entity;

import br.com.pedroxsqueiroz.Index.repo.concept.entity.ConceptEmbeddingEntity;
import br.com.pedroxsqueiroz.Index.repo.concept.entity.ConceptEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "content_chunks")
public class ContentChunckEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "content_id", nullable = false)
    private ContentEntity content;

    /**
     * Conceito ao qual este trecho pertence.
     * Nullable — um trecho pode existir antes de ser associado a um conceito.
     */
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concept_id")
    private ConceptEntity concept;

    /**
     * Vetor semântico deste trecho.
     * Lazy — carregado somente quando acessado explicitamente.
     * A relação é 1:1 e o FK (chunk_id) fica na tabela concept_embeddings.
     */
    @OneToOne(mappedBy = "chunk", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ConceptEmbeddingEntity embedding;

    @Column(nullable = false)
    private int startPos;

    @Column(nullable = false)
    private int endPos;

    @Column(nullable = false)
    private int page;
}

package br.com.pedroxsqueiroz.Index.repo.concept.entity;

import br.com.pedroxsqueiroz.Index.repo.content.entity.ContentChunckEntity;
import br.com.pedroxsqueiroz.Index.repo.converter.EmbeddingVectorConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Armazena o vetor semântico (embedding) de um ContentChunck.
 *
 * Relação 1:1 com ContentChunck — um chunk tem no máximo um embedding.
 * A tabela concept_embeddings é mantida separada para que o vetor seja
 * carregado de forma lazy (somente quando acessado via chunk.getEmbedding()).
 *
 * A coluna "embedding" usa o tipo vector(384) do pgvector.
 * O operador <=> permite busca por similaridade de cosseno.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "concept_embeddings")
public class ConceptEmbeddingEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chunk_id", nullable = false, unique = true)
    private ContentChunckEntity chunk;

    @Type(EmbeddingVectorConverter.class)
    @Column(name = "embedding", columnDefinition = "vector(384)")
    private List<Float> embedding;
}

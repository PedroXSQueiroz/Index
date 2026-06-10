package br.com.pedroxsqueiroz.Index.repo.concept;

import br.com.pedroxsqueiroz.Index.repo.concept.entity.ConceptEntity;
import br.com.pedroxsqueiroz.Index.repo.content.entity.ContentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConceptJpaRepository extends JpaRepository<ConceptEntity, String> {

    Optional<ConceptEntity> findByName(String name);

    /** Conceitos cujos chunks pertencem ao content informado. */
    List<ConceptEntity> findAllByChuncks_Content(ContentEntity content, Pageable pageable);

    /**
     * Busca conceitos por similaridade de cosseno usando pgvector.
     *
     * O operador <=> calcula a distância de cosseno (0 = idênticos, 2 = opostos).
     * A similaridade é 1 - distância, portanto tolerance é o limiar mínimo de similaridade.
     *
     * @param vector   vetor de consulta no formato pgvector: [f1,f2,...,fn]
     * @param tolerance limiar mínimo de similaridade (ex: 0.75)
     */
    @Query(value = """
            SELECT DISTINCT c.*
            FROM concepts c
            JOIN content_chunks cc ON cc.concept_id = c.id
            JOIN concept_embeddings ce ON ce.chunk_id = cc.id
            WHERE 1 - (ce.embedding <=> CAST(:vector AS vector)) >= :tolerance
            """, nativeQuery = true)
    List<ConceptEntity> findBySimilarity(@Param("vector") String vector,
                                         @Param("tolerance") float tolerance);
}

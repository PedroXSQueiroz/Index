package br.com.pedroxsqueiroz.Index.repo.concept;

import br.com.pedroxsqueiroz.Index.repo.concept.entity.ConceptEmbeddingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConceptEmbeddingJpaRepository extends JpaRepository<ConceptEmbeddingEntity, String> {

    @Query(value = """
            SELECT ce.*
              FROM concept_embeddings ce
             WHERE 1 - (ce.embedding <=> CAST(:vector AS vector)) >= :tolerance
            """, nativeQuery = true)
    List<ConceptEmbeddingEntity> findSimilar(@Param("vector") String vector,
                                             @Param("tolerance") float tolerance);
}

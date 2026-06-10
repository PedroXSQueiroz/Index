package br.com.pedroxsqueiroz.Index.repo.embedding;

import br.com.pedroxsqueiroz.Index.repo.concept.entity.ConceptEmbeddingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmbeddingRepository extends JpaRepository<ConceptEmbeddingEntity, String> {
}

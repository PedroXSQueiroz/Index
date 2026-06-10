package br.com.pedroxsqueiroz.Index.repo.content;

import br.com.pedroxsqueiroz.Index.repo.content.entity.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentJpaRepository extends JpaRepository<ContentEntity, String> {

}

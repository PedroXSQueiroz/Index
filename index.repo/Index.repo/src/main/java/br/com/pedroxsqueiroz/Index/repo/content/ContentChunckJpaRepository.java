package br.com.pedroxsqueiroz.Index.repo.content;

import br.com.pedroxsqueiroz.Index.concept.models.Content;
import br.com.pedroxsqueiroz.Index.repo.content.entity.ContentChunckEntity;
import br.com.pedroxsqueiroz.Index.repo.content.entity.ContentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentChunckJpaRepository extends JpaRepository<ContentChunckEntity, String>, JpaSpecificationExecutor<ContentChunckEntity> {

    Page<ContentChunckEntity> findAllByContent(ContentEntity content, PageRequest page);

    List<ContentChunckEntity> findAllByContentAndPageBetween(ContentEntity savedContent, int startPage, int endPage);

    int countAllByContent(ContentEntity content);

}

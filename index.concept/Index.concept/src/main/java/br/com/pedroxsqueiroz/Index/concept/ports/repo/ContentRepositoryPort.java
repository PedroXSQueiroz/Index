package br.com.pedroxsqueiroz.Index.concept.ports.repo;

import br.com.pedroxsqueiroz.Index.concept.models.Concept;
import br.com.pedroxsqueiroz.Index.concept.models.Content;
import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;
import br.com.pedroxsqueiroz.Index.concept.models.Embedding;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface ContentRepositoryPort {

    List<Concept> getRelatedConcepts(Content content);

    List<Content> containsConcepts(List<Concept> concepts);

    String getContentChunck(String contentId, int offset, int limit);

    String getContentChunck(ContentChunck contentChunck);

    Concept relateConcpet(List<ContentChunck> contentChunck, Concept concept);

    Content save(Content build);

    ContentChunck saveChunck(ContentChunck chunck);

    List<Content> findAll(int offset, int limit);

    List<ContentChunck> findContentChunckOfContent(Content content, int offset, int limit);

    List<ContentChunck> findContentChunckOfContentBetweenPages(Content savedContent, int startPage, int endPage);

    Set<ContentChunck> findSimilarContentChuncks(Embedding embedding);

    Content get(String contentId);

}

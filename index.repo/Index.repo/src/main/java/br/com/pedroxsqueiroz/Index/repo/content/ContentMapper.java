package br.com.pedroxsqueiroz.Index.repo.content;

import br.com.pedroxsqueiroz.Index.concept.constants.ContentType;
import br.com.pedroxsqueiroz.Index.concept.models.Content;
import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;
import br.com.pedroxsqueiroz.Index.repo.concept.ConceptMapper;
import br.com.pedroxsqueiroz.Index.repo.content.entity.ContentChunckEntity;
import br.com.pedroxsqueiroz.Index.repo.content.entity.ContentEntity;

public class ContentMapper {

    public static Content toDomain(ContentEntity entity) {
        if (entity == null) return null;
        return Content.builder()
                .id(entity.getId())
                .storageId(entity.getStorageId())
                .name(entity.getName())
                .description(entity.getDescription())
                .author(entity.getAuthor())
                .uploadDate(entity.getUploadDate())
                .type(ContentType.valueOf(entity.getType()))
                .build();
    }

    public static ContentEntity toEntity(Content content) {
        return ContentEntity.builder()
                .id(content.getId())
                .storageId(content.getStorageId())
                .name(content.getName())
                .description(content.getDescription())
                .author(content.getAuthor())
                .type(content.getType().toString())
                .uploadDate(content.getUploadDate())
                .build();
    }

    public static ContentChunck chunkToDomain(ContentChunckEntity entity) {
        if (entity == null) return null;
        return ContentChunck.builder()
                .id(entity.getId())
                .content(toDomain(entity.getContent()))
                .start(entity.getStartPos())
                .end(entity.getEndPos())
                .embedding(ConceptMapper.embeddingToDomain(entity.getEmbedding()))
                .build();
    }

    public static ContentChunckEntity chunkToEntity(ContentChunck chunk) {
        return ContentChunckEntity.builder()
                .id(chunk.getId())
                .startPos(chunk.getStart())
                .endPos(chunk.getEnd())
                .content(toEntity(chunk.getContent()))
                .build();
    }
}

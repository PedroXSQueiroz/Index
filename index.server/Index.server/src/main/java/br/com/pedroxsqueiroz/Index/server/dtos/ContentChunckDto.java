package br.com.pedroxsqueiroz.Index.server.dtos;

import br.com.pedroxsqueiroz.Index.concept.models.Content;
import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;
import lombok.Data;

@Data
public class ContentChunckDto {

    private String id;
    private int start;
    private int end;
    private String chunckContent;
    private ContentDto content;

    public ContentChunckDto(ContentChunck chunck)
    {
        this.id     = chunck.getId();
        this.start  = chunck.getStart();
        this.end    = chunck.getEnd();
        this.content = new ContentDto(chunck.getContent());
    }

    public ContentChunckDto(ContentChunck chunck, ContentDto content)
    {
        this.id     = chunck.getId();
        this.start  = chunck.getStart();
        this.end    = chunck.getEnd();
        this.content = content;
    }

    public ContentChunckDto(ContentChunck chunck, String chunckContent, ContentDto content)
    {
        this(chunck, content);
        this.chunckContent = chunckContent;
    }

}

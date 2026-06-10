package br.com.pedroxsqueiroz.Index.server.dtos;

import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;
import lombok.Data;

@Data
public class ContentChunckDto {

    private String id;
    private int start;
    private int end;
    private String content;

    public ContentChunckDto(ContentChunck chunck)
    {
        this.id     = chunck.getId();
        this.start  = chunck.getStart();
        this.end    = chunck.getEnd();
    }

    public ContentChunckDto(ContentChunck chunck, String content)
    {
        this(chunck);
        this.content = content;
    }

}

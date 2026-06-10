package br.com.pedroxsqueiroz.Index.server.dtos;

import br.com.pedroxsqueiroz.Index.concept.models.Content;
import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class ContentChuncksContainer {
        private int startPage;
        private int endPage;
        private List<ContentChunckDto> chuncksList;
    }

    private String id;
    private String name;
    private String description;
    private String type;
    private String author;
    private Date uploadDate;
    private ContentChuncksContainer chuncks;

    Content toModel()
    {
        return Content.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .author(this.author)
                .type(this.type)
                .uploadDate(this.uploadDate)
                .build();
    }

    public ContentDto(Content content)
    {
        this.id = content.getId();
        this.name = content.getName();
        this.description = content.getDescription();
        this.type = content.getType();
        this.author = content.getAuthor();
        this.uploadDate = content.getUploadDate();
        this.chuncks = null;
    }

    public ContentDto(Content content, List<ContentChunck> chuncks, int startPage, int endPage)
    {
        this(content);
        this.chuncks = ContentChuncksContainer.builder()
                .chuncksList(chuncks.stream()
                                    .map(ContentChunckDto::new)
                                    .toList()
                ).startPage(startPage)
                .endPage(endPage)
                .build();
    }

}

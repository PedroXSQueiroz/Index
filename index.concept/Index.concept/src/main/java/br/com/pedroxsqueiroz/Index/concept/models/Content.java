package br.com.pedroxsqueiroz.Index.concept.models;

import br.com.pedroxsqueiroz.Index.concept.constants.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Content {

    private String id;
    private String storageId;
    private String name;
    private String description;
    private String author;
    private ContentType type;
    private String storage;
    int pagesCount;
    private Date uploadDate;

}

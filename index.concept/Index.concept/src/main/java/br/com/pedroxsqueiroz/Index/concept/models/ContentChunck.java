package br.com.pedroxsqueiroz.Index.concept.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentChunck {

    private String id;
    private Content content;
    private int start;
    private int end;
    private int page;
    private Embedding embedding;

    public ContentChunck withPage(int page){
        this.page = page;
        return this;
    }

}

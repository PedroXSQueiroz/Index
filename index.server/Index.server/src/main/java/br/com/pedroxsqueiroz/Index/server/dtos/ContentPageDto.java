package br.com.pedroxsqueiroz.Index.server.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentPageDto {

    String content;

    List<ContentChunckDto> chuncks;

}

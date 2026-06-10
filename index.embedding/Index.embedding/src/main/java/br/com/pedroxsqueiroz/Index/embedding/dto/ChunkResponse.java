package br.com.pedroxsqueiroz.Index.embedding.dto;

import lombok.Data;

@Data
public class ChunkResponse {
    private int offset;
    private int limit;
}

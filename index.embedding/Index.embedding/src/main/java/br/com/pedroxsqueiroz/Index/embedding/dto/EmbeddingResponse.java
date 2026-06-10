package br.com.pedroxsqueiroz.Index.embedding.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmbeddingResponse {
    private List<Float> embedding;
}

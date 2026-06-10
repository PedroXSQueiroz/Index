package br.com.pedroxsqueiroz.Index.concept.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Embedding {

    private List<Float> vector;

    /** Constrói a partir de uma lista já existente. */
    public static Embedding of(List<Float> vector) {
        return new Embedding(new ArrayList<>(vector));
    }

    /** Constrói a partir de valores literais (conveniente em testes). */
    public static Embedding of(float... values) {
        List<Float> list = new ArrayList<>(values.length);
        for (float v : values) list.add(v);
        return new Embedding(list);
    }
}

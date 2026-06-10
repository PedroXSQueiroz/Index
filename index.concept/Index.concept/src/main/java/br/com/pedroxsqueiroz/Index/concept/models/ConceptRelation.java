package br.com.pedroxsqueiroz.Index.concept.models;

import br.com.pedroxsqueiroz.Index.concept.constants.ConceptRelationTypesEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class ConceptRelation {

    private Concept origin;

    private ConceptRelationTypesEnum type;

    private Concept target;

    public boolean equals(Object other) {
        if(this == other) return true;
        if(!(other instanceof ConceptRelation)) return false;
        ConceptRelation that = (ConceptRelation) other;
        return      that.getOrigin().equals(this.getOrigin())
                &&  that.getType().equals(this.getType())
                &&  that.getTarget().equals(this.getTarget());
    }

    public int hashCode() {
        return Objects.hash(origin, type, target);
    }

    public String toString()
    {
        return String.format("%s -> [%s] -> %s",origin.toString(),type.toString(),target.toString());
    }

}

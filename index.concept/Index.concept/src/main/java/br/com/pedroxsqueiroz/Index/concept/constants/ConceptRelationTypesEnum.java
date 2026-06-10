package br.com.pedroxsqueiroz.Index.concept.constants;

public enum ConceptRelationTypesEnum {

    IS_CAUSE(1), //IMPLIES ANOTHER CONCEPT IS CONSEQUENCE
    IS_PART_OF(2), //IMPLIES ANOTHER CONCEPT INCLUDES IT
    OPPOSES(3),
    IS_SIMILAR(4),
    COMPLEMENTS(5),
    IS_BEFORE_OF(6), //IMPLIES ANOTHER CONCEPT IS AFTER IT

    IS_CAUSED_BY(-1),
    INCLUDES(-2),
    IS_AFTER_OF(-6);

    ConceptRelationTypesEnum(int relationIndex) {
        this.relationKey = relationIndex;
    }

    private int relationKey;

    public boolean isInverse() {
        return this.relationKey < 0;
    }

    public int getRelationKey() {
        return this.relationKey;
    }

    public ConceptRelationTypesEnum getInverseRelation()
    {
        int relationInverseKey = this.relationKey * -1;

        ConceptRelationTypesEnum[] types = ConceptRelationTypesEnum.values();
        for(ConceptRelationTypesEnum currentType : types)
        {
            if(currentType.relationKey == relationInverseKey)
            {
                return currentType;
            }
        }

        return null;
    }

}

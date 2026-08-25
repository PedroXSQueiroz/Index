import ConceptDto from '../dtos/concept-dto';

import './concept-resume.css'

export default function ConceptResume(props){

    let concept:ConceptDto = props.concept;

    //FIXME: relatedConcepts ainda nao existe no ConceptDto nem no service.
    //A linha de conexoes fica vazia ate o campo entrar.
    let relatedConcepts = concept.relatedConcepts ?? [];

    return (
    <div className="concept">
        <h2 className="concept-name">
            <span className={`with-marker marker-tint-${props.markerTint ?? 1}`}>{concept.name}</span>
        </h2>
        <div className="concept-rule"></div>
        <p className="concept-description">{concept.descrption}</p>
        <p className="concept-links">
            {relatedConcepts.length} conexões — {relatedConcepts.map(related => related.name).join(', ')}
        </p>
    </div>
    );
};

import { useState, useEffect } from "react";
import { useSearchParams } from "react-router";

import ContentService from "../../services/content-service"
import ConceptDto from "../../dtos/concept-dto";
import ContentContainer from "../../components/content-container";
import type ContentChunckDto from "../../dtos/content-chunck-dto";

import './concepts-page.css';
import ConceptResume from "../../components/concept-resume";

export default function ConceptsPage(){

    const [conceptsState, setConceptsState] = useState([]);
    // const [currentContent, setCurrentContent] = useState([]);
    const [chuncksOfCurrentContent, setChuncksOfCurrentContent] = useState([]);
    const [queryParams] = useSearchParams();
    
    let contentService:ContentService = new ContentService();
    let contentIds:string[]|null = queryParams.getAll('content');

    //`getAll` devolve um array novo a cada render, entao a dependencia do effect
    //precisa ser um primitivo estavel — do contrario o effect roda em todo ciclo.
    let contentIdsKey:string = contentIds.join(',');

    let selectMostRecentContent = (concepts: ConceptDto[]) =>
    {
        //SORT ALL CHUNKS OF EACH CONCEPT
        concepts.forEach( c => 
            c.chuncks.sort( (currentChunck, comparingChunck) => {
                return  comparingChunck.content.uploadDate.getTime()
                    -   currentChunck.content.uploadDate.getTime();
            }
        ));
        
        //SORT ALL CONCEPTS BY THE MOST RECENT CONTENT OF CHUNCK LIST ORDERED
        concepts.sort( (current, comparing) => {
            return  comparing.chuncks[0].content.uploadDate.getTime()
                -   current.chuncks[0].content.uploadDate.getTime();
        });

        //GET CONCEPT WITH CONTENT MOST RECENT
        //ORDERING FIRST CHUNCKS AND AFTER THE CONCEPTS AVOID
        //REDO THE ORDERING MANY TIMES 
        return concepts[0].chuncks[0].content.id;
    }

    //FIXME: PROVISORY LOGIC TO GET ALL CONCEPTS
    //MAYBE USE GRAPHQL?
    useEffect(() => {

        if(!contentIds){
            return;
        }

        //Descarta o resultado quando o effect e desmontado antes da resposta chegar
        //(troca de `?content=` e duplo-mount do StrictMode em dev).
        let cancelled:boolean = false;

        let fetchConceptsOfContents = new Promise( async (resolve, reject) => {

            let concepts:ConceptDto[] = [];

            for(let contentId of contentIds)
            {
                let conceptsOfContent:ConceptDto[] = await contentService.getConceptsOfContent(contentId);
                concepts = concepts.concat( conceptsOfContent );
            }

            resolve(concepts);
        });

        Promise.all([
            fetchConceptsOfContents
        ]).then( async allConcepts => {

            if(cancelled){
                return;
            }

            let concepts:ConceptDto[] = allConcepts.reduce((aggr, current) => aggr.concat(current), []);
            let mostRecentContentId:string = selectMostRecentContent(concepts);
            let chuncks:ContentChunckDto[] = await contentService.getChuncksOfContent(mostRecentContentId);

            if(cancelled){
                return;
            }

            setConceptsState(concepts);
            setChuncksOfCurrentContent(chuncks);

        });

        return () => { cancelled = true; };

    }, [contentIdsKey]);

    let existsConcepts = conceptsState && conceptsState.length > 0;

    return existsConcepts ? (
            <div className="concepts-page">
                <div className="contents-panel">
                    <ContentContainer waitingInput={false} chuncksOfContent={chuncksOfCurrentContent} />
                </div>
                <div className="concepts-panel">
                    {conceptsState.map(con => <ConceptResume concept={con} />)}
                </div>
            </div>
        ): ( <h2>Loading</h2> );
    
        
    

}
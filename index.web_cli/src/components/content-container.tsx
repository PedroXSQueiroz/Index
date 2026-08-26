import React from 'react';
import { useEffect } from 'react';
import { useNavigate, useSearchParams } from "react-router";

import ContentService from '../services/content-service';
import ConceptDto from '../dtos/concept-dto';

import './content-container.css'

export default function ContentContainer(props){
    
    let textAreaContentRef  = React.createRef();
    const navigate          = useNavigate();
    const queryParams       = useSearchParams();
    
    let submitContentPlainText = async () => {
        let contentService:ContentService = new ContentService();
        let contentId = await contentService.submitPlainText(textAreaContentRef.current.value);
        await contentService.processEmbeddingsOfContent(contentId);
        await contentService.generateConceptsOfContent(contentId);
        
        navigate(`/concepts?content=${contentId}`);
    }
    
    return (
    <div className="content-input-box">
        {props.waitingInput ? 
        <>
            <textarea
            ref={textAreaContentRef}
            className="content-input"
            placeholder="Cole ou escreva o texto a ser indexado."
            />
            <div className="content-input-actions">
            <label className="attach-label">
                <svg
                width="14"
                height="14"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="1.4"
                strokeLinecap="round"
                strokeLinejoin="round"
                >
                <path d="M12 19V5" />
                <path d="m5 12 7-7 7 7" />
                </svg>
                <span>Anexar documento</span>
                <input type="file" />
            </label>
            <button className="btn-index" onClick={() => submitContentPlainText()}>Indexar</button>
            </div>
        </> : props.chuncksOfContent && !props.waitingInput && 
            <div className="content-input">
                {props.chuncksOfContent.map( chunck => <span>{chunck.chunckContent}</span> )}
            </div>
        }
      </div>
      );
};
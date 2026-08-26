import ConceptDto from "../dtos/concept-dto";
import ContentChunckDto from "../dtos/content-chunck-dto";
import ContentDto from "../dtos/content-dto";

//FIXME: esta URL deve vir de uma configuração obtida de forma mais eficiente (variável de ambiente / arquivo de config), não de uma constante hardcoded
const API_ROOT_URL:string = 'http://localhost:8080';

export default class ContentService{

    static contentLastId:number = 0;
    static dummyContentSplitterCharCount:number = 50;
    static dummyConceptNameCharCount:number = 10;
    static dummyConceptDescriptionCharCount:number = 20;

    async getContent(contentId:string)
    {
        return new ContentDto(
            contentId,
            'Inferido pelo sistema',
            'Inferido pelo sistema também',
            'PLAINT_TEXT'
        );
    }

    async getChuncksOfContent(contentId:string, page:number = 0): Promise<ContentChunckDto[]>
    {
        let response = await fetch(`${API_ROOT_URL}/content/${contentId}/page/${page}`);
        
        let responseData = await response.json();

        return responseData.chuncks.map( chunck =>
            new ContentChunckDto( 
                chunck.id,
                chunck.start,
                chunck.end,
                chunck.chunckContent,
                null
            )
        );
    }
    
    async getConceptsOfContent(contentId: string) : Promise<ConceptDto[]>
    {
        let response = await fetch(`${API_ROOT_URL}/concept/?content=${contentId}&offset=0&limit=20`, { method: 'GET' } );

        let responseBody = await response.json();
        return responseBody.map(currentConceptData => {
            return new ConceptDto(
                currentConceptData.id,
                currentConceptData.name,
                currentConceptData.description,
                currentConceptData.chuncks.map(chunck => new ContentChunckDto(
                    chunck.id,
                    chunck.start,
                    chunck.end,
                    '',
                    new ContentDto(
                        chunck.content.id,
                        chunck.content.name,
                        chunck.content.description,
                        chunck.content.type,
                        new Date( chunck.content.uploadDate )
                    )
                ))
            );
        });

    }

    async submitPlainText(content: string) : Promise<string>
    {
        //FIXME: name, description e author são valores fixos provisórios; devem vir da UI
        let response = await fetch(`${API_ROOT_URL}/content/`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                content: content,
                name: 'Inferido pelo sistema',
                description: 'Inferido pelo sistema também',
                author: '',
                type: 'PLAIN_TEXT'
            })
        });

        let responseBody = await response.json();

        let savedContent:ContentDto = new ContentDto(
            responseBody.id,
            responseBody.name,
            responseBody.description,
            responseBody.type
        );

        return savedContent.id;
    }

    async processEmbeddingsOfContent(contentId: string)
    {
        await fetch(`${API_ROOT_URL}/content/${contentId}/embeddings`, {
            method: 'POST'
        });
    }

    async generateConceptsOfContent(contentId: string): Promise<ConceptDto[]>
    {
        let response = await fetch(`${API_ROOT_URL}/content/${contentId}/concept`, {
            method: 'POST'
        });

        let responseBody = await response.json();

        return responseBody.map( concept => new ConceptDto(
            concept.id,
            concept.name,
            concept.description,
            concept.chuncks.map( chunck => new ContentChunckDto(
                chunck.id,
                chunck.start,
                chunck.end,
                chunck.chunckContent,
                new ContentDto(
                    chunck.content.id,
                    chunck.content.name,
                    chunck.content.description,
                    chunck.content.type
                )
            ))
        ));
    }

}
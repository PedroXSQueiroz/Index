import ConceptDto from "../dtos/concept-dto";
import ContentChunckDto from "../dtos/content-chunck-dto";

export default class ContentService{

    static contentLastId:number = 0;
    static dummyContentSplitterCharCount:number = 50;
    static dummyConceptNameCharCount:number = 10;
    static dummyConceptDescriptionCharCount:number = 20;

    async submitPlainText(content: string) {
        
        let currentId = (++ContentService.contentLastId).toString();

        localStorage.setItem(currentId, content);
        
        return currentId;
    }

    async processEmbeddingsOfContent(contentId: string)
    {
        let content:string|null = localStorage.getItem(contentId);
        let chunck_count:number = 0;

        while(content)
        {
            localStorage.setItem( `${contentId}-${++chunck_count}`, content.slice(0, ContentService.dummyContentSplitterCharCount));
            content = content.slice(ContentService.dummyContentSplitterCharCount);
        }
    }

    async generateConceptsOfContent(contentId: string)
    {
        let chunck_count:number = 1;
        let currentChunckId:string = `${contentId}-${chunck_count}`;
        let currentChunck = localStorage.getItem(currentChunckId);
        let concepts:ConceptDto[] = [];

        while(currentChunck)
        {
            concepts.push(new ConceptDto(
                currentChunckId,
                currentChunck.slice(0, ContentService.dummyConceptNameCharCount),
                currentChunck.slice(0, ContentService.dummyConceptDescriptionCharCount),
                [
                    new ContentChunckDto(
                        currentChunckId,
                        chunck_count * ContentService.dummyContentSplitterCharCount,
                        ( chunck_count * ContentService.dummyContentSplitterCharCount ) + chunck_count * ContentService.dummyContentSplitterCharCount,
                        currentChunck
                    ),
                ]
            ));
            
            currentChunckId = `${contentId}-${++chunck_count}`;
            currentChunck = localStorage.getItem(currentChunckId);
        }

        return concepts;

    }

}
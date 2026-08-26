import type ContentDto from "./content-dto";

export default class ContentChunckDto{
    
    constructor(
        id: string,
        start: number,
        end: number,
        chunckContent: string,
        content: ContentDto|null

    ){
        this.id = id;
        this.start = start;
        this.end = end;
        this.chunckContent = chunckContent;
        this.content = content;
    }

    id: string;
    start: number;
    end: number;
    chunckContent: string;    
    content: ContentDto|null;
}
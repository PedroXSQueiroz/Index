export default class ContentChunckDto{
    
    constructor(
        id: string,
        start: number,
        end: number,
        content: string
    ){
        this.id = id;
        this.start = start;
        this.end = end;
        this.content = content;
    }

    id: string;
    start: number;
    end: number;
    content: string;
}
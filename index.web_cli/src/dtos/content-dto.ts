export default class ContentDto{
    
    constructor(
        id: string,
        name: string,
        description: string,
        type: string,
        uploadDate: Date = new Date()
    ){
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    id: string;
    name: string;
    description: string;
    type: string;
    author: string = '';
    uploadDate: Date = new Date();
}
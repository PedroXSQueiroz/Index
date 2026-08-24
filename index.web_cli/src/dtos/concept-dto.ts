import ContentChunckDto from "./content-chunck-dto";

export default class ConceptDto{

    constructor(
        id:string,
        name:string,
        descrption:string,
        chuncks: ContentChunckDto[]
    ){
        this.id = id;
        this.name = name;
        this.descrption = descrption;
        this.chuncks = chuncks;
    }

    id:string ;
    name:string ;
    descrption:string ;
    chuncks: ContentChunckDto[] ;

}
package br.com.pedroxsqueiroz.Index.server.controllers;

import br.com.pedroxsqueiroz.Index.concept.constants.ContentType;
import br.com.pedroxsqueiroz.Index.concept.exceptions.InconsistentContentRequestException;
import br.com.pedroxsqueiroz.Index.concept.models.Concept;
import br.com.pedroxsqueiroz.Index.concept.models.Content;
import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;
import br.com.pedroxsqueiroz.Index.concept.services.ConceptService;
import br.com.pedroxsqueiroz.Index.concept.services.ContentService;
import br.com.pedroxsqueiroz.Index.concept.services.EmbeddingService;
import br.com.pedroxsqueiroz.Index.server.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private EmbeddingService embeddingService;

    @PostMapping(value = "/", consumes = "application/json")
    @ResponseBody
    ResponseEntity<?> savePlainText(@RequestBody PlainTextContentDto content)
    {
        //FIXME: VALIDATE IF TYPE IS VALID

        Content savedContent = contentService.saveContent(
                List.of(content.getContent()),
                content.getName(),
                content.getDescription(),
                content.getAuthor(),
                ContentType.valueOf(content.getType())
        );

        List<ContentChunck> chuncks = contentService.getContentChunck(savedContent, 0 , 1);

        return  ResponseEntity.ok( new ContentDto(savedContent, chuncks, 0 , 1) );
    }

    @PostMapping(value = "/{contentId}/embeddings")
    @ResponseBody
    ResponseEntity<?> processEmbeddings(@PathVariable String contentId)
    {
        Content content = contentService.getContent(contentId);

        contentService.generateEmbeddingsOf(content);

        return  ResponseEntity.ok().build();
    }

    @PostMapping("/{contentId}/concept")
    @ResponseBody
    ResponseEntity<?> buildConcepts(@PathVariable String contentId)
    {
        Content contentFound = contentService.getContent(contentId);
        if(contentFound == null)
        {
            return  ResponseEntity.notFound().build();
        }

        List<Concept> concepts = conceptService.buildConceptsForContent(contentFound);

        List<ConceptDto> conceptsDto = concepts.stream()
            .map( concept -> {

                List<ContentChunckDto> chunckDtos = concept.getChuncks()
                    .stream()
                    .filter(chunck -> chunck.getPage() == 0)
                    .map( chunck-> {
                        try {
                            return new ContentChunckDto(chunck, contentService.getChunckContent(List.of( chunck )));
                        } catch (InconsistentContentRequestException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();

                return new ConceptDto(concept, chunckDtos);
            }).toList();

        return ResponseEntity.ok().body(conceptsDto);
    }

    @GetMapping(value = "/")
    @ResponseBody
    ResponseEntity<?> listContent(@RequestParam int offset, @RequestParam int limit)
    {
        return ResponseEntity.ok(
            contentService.list(offset, limit).stream()
            .map(ContentDto::new)
            .toList()
        );
    }

    @GetMapping(value = "/{id}/page/{page}")
    @ResponseBody
    ResponseEntity<?> getPageContent(@PathVariable String id, @PathVariable int page)
    {

        Content content = contentService.getContent(id);

        if(content == null)
        {
            return ResponseEntity.notFound().build();
        }

        String pageContent = contentService.getPage(content, page);

        List<ContentChunck> pageChuncks = contentService.getContentChunck(content, page - 1, page);

        return ResponseEntity.ok(
            ContentPageDto.builder()
            .content(pageContent)
            .chuncks( pageChuncks.stream().map( ContentChunckDto::new ).toList() )
            .build()
        );
    }
}

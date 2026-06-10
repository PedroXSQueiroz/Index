package br.com.pedroxsqueiroz.Index.server.controllers;

import br.com.pedroxsqueiroz.Index.concept.models.Concept;
import br.com.pedroxsqueiroz.Index.concept.models.Content;
import br.com.pedroxsqueiroz.Index.concept.services.ConceptService;
import br.com.pedroxsqueiroz.Index.concept.services.ContentService;
import br.com.pedroxsqueiroz.Index.server.dtos.ConceptDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping(value = "/concept")
@RestController
public class ConceptController {

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private ContentService contentService;

    @GetMapping(value = "/")
    @ResponseBody
    public ResponseEntity<?> list(
            @RequestParam("ofsset") Integer offset,
            @RequestParam("limit") Integer limit,
            @RequestParam(value = "content", required = false) String contentId
        )
    {
        List<Concept> concepts = new ArrayList<>();
        if(contentId != null)
        {
            Content contentOwner = contentService.getContent(contentId);
            if(contentOwner != null)
            {
                concepts = conceptService.listOfContent(contentOwner, offset, limit);
            }
        }else{
            concepts = conceptService.list(offset, limit);
        }

        return ResponseEntity.ok(
            concepts.stream()
            .map( ConceptDto::new )
            .toList()
        );
    }



}

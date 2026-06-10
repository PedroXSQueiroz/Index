package br.com.pedroxsqueiroz.Index.concept.services;

import br.com.pedroxsqueiroz.Index.concept.constants.ConceptRelationTypesEnum;
import br.com.pedroxsqueiroz.Index.concept.models.Concept;
import br.com.pedroxsqueiroz.Index.concept.models.ConceptRelation;
import br.com.pedroxsqueiroz.Index.concept.models.Content;
import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;
import br.com.pedroxsqueiroz.Index.concept.models.Embedding;
import br.com.pedroxsqueiroz.Index.concept.ports.concept.ConceptHelperPort;
import br.com.pedroxsqueiroz.Index.concept.ports.embedding.EmbeddingFactoryPort;
import br.com.pedroxsqueiroz.Index.concept.ports.repo.ConceptRelationRepositoryPort;
import br.com.pedroxsqueiroz.Index.concept.ports.repo.ConceptRepositoryPort;
import br.com.pedroxsqueiroz.Index.concept.ports.repo.ContentRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConceptService {

    float minTollerance;

    private ConceptRepositoryPort conceptRepo;

    private ConceptHelperPort conceptHelper;

    private ConceptRelationRepositoryPort relationRepo;

    private EmbeddingFactoryPort embeddingFactory;

    private ContentRepositoryPort contentRepo;



    private int chuncksBatchConceptProcess;

    // public ConceptService() {}

    public ConceptService(@Autowired ConceptRepositoryPort conceptRepo,
                          @Autowired ConceptRelationRepositoryPort relationRepo,
                          @Autowired EmbeddingFactoryPort embeddingFactory,
                          @Autowired ContentRepositoryPort contentRepo,
                          @Autowired ConceptHelperPort conceptHelper,
                          @Value("${min_tollerance}") float minTollerance,
                          @Value("${chuncks_batch_concept_process}") int chuncksBatchConceptProcess )
    {
        this.conceptRepo = conceptRepo;
        this.conceptHelper = conceptHelper;
        this.relationRepo = relationRepo;
        this.minTollerance = minTollerance;
        this.embeddingFactory = embeddingFactory;
        this.contentRepo = contentRepo;
        this.chuncksBatchConceptProcess = chuncksBatchConceptProcess;
    }

    public List<Concept> findConcepts(Embedding embedding) {
        return conceptRepo.find(embedding, minTollerance);
    }

    /**
     * Retorna os conceitos conectados a {@code concept} pelo tipo de relação {@code relationType}.
     *
     * Se {@code relationType} for um tipo canônico (isInverse() == false), retorna os conceitos
     * presentes como target nas relações desse tipo em que {@code concept} é o origin.
     *
     * Se {@code relationType} for um tipo inverso (isInverse() == true), usa getInverseRelation()
     * para obter o tipo canônico e retorna os conceitos presentes como origin nas relações desse
     * tipo em que {@code concept} é o target.
     */
    public List<Concept> findConcepts(Concept concept, ConceptRelationTypesEnum relationType) {

        List<ConceptRelation> relationsContainingConcept = !relationType.isInverse() ?
                relationRepo.findByOrigin(concept, relationType) :
                relationRepo.findByTarget(concept, relationType);

        List<Concept> concepts = relationsContainingConcept.stream()
                .map(relation -> relationType.isInverse() ?
                        relation.getOrigin() :
                        relation.getTarget()
                ).toList();

        return concepts;
    }

    public Concept saveConcept(String name, String description, Set<ContentChunck> contentsChuncks) {

        Concept newConcept = Concept.builder()
            .name(name)
            .description(description)
            .chuncks(contentsChuncks)
            .build();

        return conceptRepo.save(newConcept);
    }

    /**
     * Cria uma relação tipada de {@code origin} para {@code target} e persiste via repositório.
     * Retorna o {@code origin} atualizado com a nova relação adicionada à sua lista.
     */
    public ConceptRelation correlate(Concept origin, ConceptRelationTypesEnum type, Concept target) {
        return conceptRepo.saveConceptRelation(origin, type, target);
    }

    public List<Concept> list(Integer offset, Integer limit) {
        return conceptRepo.list(offset, limit);
    }

    public List<Concept> listOfContent(Content content, Integer offset, Integer limit) {
        return conceptRepo.list(content, offset, limit);
    }

    public Concept update(Concept concept) {
        return conceptRepo.save(concept);
    }

    public List<Concept> buildConceptsForContent(Content content) {

        int offset = 0;
        int limit = chuncksBatchConceptProcess;

        List<Concept> concepts = new ArrayList<>();
        List<ContentChunck> chuncksBuffer = new ArrayList<ContentChunck>();

        do{

            chuncksBuffer = contentRepo.findContentChunckOfContent(content, offset, limit);

            for(ContentChunck chunck : chuncksBuffer){

                List<Concept> registeredConcept = findConcepts(chunck.getEmbedding());

                if( registeredConcept.isEmpty() )
                {
                    //CRIAR CONCEPT
                    Set<ContentChunck> similarChuncks = contentRepo.findSimilarContentChuncks(chunck.getEmbedding());
                    similarChuncks.add(chunck);
                    String conceptDescription   = conceptHelper.generateConceptDescription(similarChuncks.stream().toList());
                    String conceptName          = conceptHelper.generateConceptName(similarChuncks.stream().toList());

                    Concept newConcept = Concept.builder()
                            .name(conceptName)
                            .description(conceptDescription)
                            .chuncks(similarChuncks)
                            .build();

                    concepts.add(
                        conceptRepo.save(newConcept)
                    );

                }
                else
                {
                    concepts.addAll(
                        registeredConcept.stream()
                            .filter(concepts::contains)
                            .collect(Collectors.toList()
                        )
                    );
                }
            }

            offset += chuncksBatchConceptProcess;
            limit += chuncksBatchConceptProcess;

        }while(!chuncksBuffer.isEmpty());

        return concepts;
    }
}

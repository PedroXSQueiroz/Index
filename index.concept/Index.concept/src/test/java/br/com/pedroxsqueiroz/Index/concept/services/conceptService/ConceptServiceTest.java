package br.com.pedroxsqueiroz.Index.concept.services.conceptService;


import br.com.pedroxsqueiroz.Index.concept.constants.ConceptRelationTypesEnum;
import br.com.pedroxsqueiroz.Index.concept.models.Concept;
import br.com.pedroxsqueiroz.Index.concept.models.ConceptRelation;
import br.com.pedroxsqueiroz.Index.concept.models.Content;
import br.com.pedroxsqueiroz.Index.concept.models.ContentChunck;
import br.com.pedroxsqueiroz.Index.concept.models.Embedding;
import br.com.pedroxsqueiroz.Index.concept.ports.embedding.EmbeddingFactoryPort;
import br.com.pedroxsqueiroz.Index.concept.ports.repo.ConceptRelationRepositoryPort;
import br.com.pedroxsqueiroz.Index.concept.ports.repo.ConceptRepositoryPort;
import br.com.pedroxsqueiroz.Index.concept.ports.repo.ContentRepositoryPort;
import br.com.pedroxsqueiroz.Index.concept.services.ConceptService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ConceptServiceTest {

    @Mock
    private ConceptRepositoryPort conceptRepo;

    @Mock
    private ConceptRelationRepositoryPort relationRepo;

    @Mock
    private ContentRepositoryPort contentRepo;

    @Mock
    private EmbeddingFactoryPort embeddingFactory;

    @InjectMocks
    private ConceptService conceptService;

    private static final int DERIVADA      = 0;
    private static final int LIMITE        = 1;
    private static final int FUNCAO        = 2;
    private static final int CONTINUIDADE  = 3;
    private static final int TAXA_VARIACAO = 4;
    private static final int NUMERO_REAL   = 5;
    private static final int INTEGRAL      = 6;
    private static final int SEQUENCIA     = 7;
    private static final int ALGEBRA       = 8;
    private static final int TRIGONOMETRIA = 9;

    private static final int NUMERO_REAL_IS_BEFORE_OF_ALGEBRA        = 0;
    private static final int NUMERO_REAL_IS_PART_OF_FUNCAO           = 1;
    private static final int ALGEBRA_IS_CAUSE_FUNCAO                 = 2;
    private static final int NUMERO_REAL_IS_BEFORE_OF_SEQUENCIA      = 3;
    private static final int SEQUENCIA_IS_PART_OF_ALGEBRA            = 4;
    private static final int SEQUENCIA_IS_CAUSE_LIMITE               = 5;
    private static final int ALGEBRA_IS_BEFORE_OF_FUNCAO             = 6;
    private static final int FUNCAO_IS_CAUSE_TAXA_VARIACAO           = 7;
    private static final int FUNCAO_IS_BEFORE_OF_LIMITE              = 8;
    private static final int NUMERO_REAL_IS_BEFORE_OF_LIMITE         = 9;
    private static final int LIMITE_IS_CAUSE_CONTINUIDADE            = 10;
    private static final int FUNCAO_IS_BEFORE_OF_CONTINUIDADE        = 11;
    private static final int CONTINUIDADE_IS_BEFORE_OF_DERIVADA      = 12;
    private static final int FUNCAO_IS_BEFORE_OF_TAXA_VARIACAO       = 13;
    private static final int NUMERO_REAL_IS_BEFORE_OF_TAXA_VARIACAO  = 14;
    private static final int TAXA_VARIACAO_IS_SIMILAR_DERIVADA       = 15;
    private static final int FUNCAO_IS_BEFORE_OF_TRIGONOMETRIA       = 16;
    private static final int ALGEBRA_IS_BEFORE_OF_TRIGONOMETRIA      = 17;
    private static final int NUMERO_REAL_IS_BEFORE_OF_TRIGONOMETRIA  = 18;
    private static final int LIMITE_IS_BEFORE_OF_DERIVADA            = 19;
    private static final int FUNCAO_IS_BEFORE_OF_DERIVADA            = 20;
    private static final int DERIVADA_IS_CAUSE_INTEGRAL              = 21;
    private static final int LIMITE_IS_BEFORE_OF_INTEGRAL            = 22;
    private static final int FUNCAO_IS_BEFORE_OF_INTEGRAL            = 23;
    private static final int SEQUENCIA_IS_SIMILAR_FUNCAO             = 24;
    private static final int LIMITE_IS_SIMILAR_CONTINUIDADE          = 25;
    private static final int DERIVADA_IS_SIMILAR_INTEGRAL            = 26;
    private static final int TAXA_VARIACAO_IS_SIMILAR_INTEGRAL       = 27;

    private final  List<Concept>  dummyConcepts = List.of(
        Concept.builder()
                .id("0")
                .name("Derivada")
                .description("Taxa de variação instantânea de uma função em um ponto.")
                .build(),
        Concept.builder()
                .id("1")
                .name("Limite")
                .description("Valor ao qual uma função se aproxima conforme o argumento se aproxima de um ponto.")
                .build(),
        Concept.builder()
                .id("2")
                .name("Função")
                .description("Relação que associa cada elemento de um conjunto a exatamente um elemento de outro conjunto.")
                .build(),
        Concept.builder()
                .id("3")
                .name("Continuidade")
                .description("Propriedade de uma função que não apresenta saltos ou interrupções em seu domínio.")
                .build(),
        Concept.builder()
                .id("4")
                .name("Taxa de variação")
                .description("Razão entre a variação de uma grandeza e a variação de outra grandeza relacionada.")
                .build(),
        Concept.builder()
                .id("5")
                .name("Número real")
                .description("Elemento do conjunto que engloba racionais e irracionais, base do cálculo diferencial.")
                .build(),
        Concept.builder()
                .id("6")
                .name("Integral")
                .description("Operação inversa à derivada; calcula a área sob a curva de uma função.")
                .build(),
        Concept.builder()
                .id("7")
                .name("Sequência")
                .description("Lista ordenada de elementos que segue uma regra de formação.")
                .build(),
        Concept.builder()
                .id("8")
                .name("Álgebra")
                .description("Ramo da matemática que estuda operações e estruturas abstratas.")
                .build(),
        Concept.builder()
                .id("9")
                .name("Trigonometria")
                .description("Estudo das relações entre ângulos e lados de triângulos.")
                .build()
    );

    private List<ConceptRelation> dummyRelations;

    /*
     * Grafo de relações entre os conceitos dummy
     *
     * Leitura: ORIGIN --[TIPO]--> TARGET
     *
     * NUMERO_REAL  --[IS_BEFORE_OF]-->  ALGEBRA
     * NUMERO_REAL  --[IS_PART_OF]-->    FUNCAO          (reais compõem o domínio/contradomínio de funções)
     *
     * NUMERO_REAL  --[IS_BEFORE_OF]-->  ALGEBRA
     * ALGEBRA      --[IS_CAUSE]-->      FUNCAO          (álgebra formaliza o conceito de função)
     *
     * NUMERO_REAL  --[IS_BEFORE_OF]-->  SEQUENCIA
     * SEQUENCIA    --[IS_PART_OF]-->    ALGEBRA         (sequências são objeto de estudo da álgebra)
     * SEQUENCIA    --[IS_CAUSE]-->      LIMITE          (limite de sequências motiva o conceito de limite)
     * SEQUENCIA    --[IS_SIMILAR]-->    FUNCAO          (sequência é uma função cujo domínio são os naturais)
     *
     * ALGEBRA      --[IS_BEFORE_OF]-->  FUNCAO
     * NUMERO_REAL  --[IS_PART_OF]-->    FUNCAO
     * FUNCAO       --[IS_CAUSE]-->      TAXA_VARIACAO   (funções habilitam o estudo de variação)
     *
     * FUNCAO       --[IS_BEFORE_OF]-->  LIMITE
     * NUMERO_REAL  --[IS_BEFORE_OF]-->  LIMITE
     * SEQUENCIA    --[IS_CAUSE]-->      LIMITE
     * LIMITE       --[IS_CAUSE]-->      CONTINUIDADE    (continuidade é definida formalmente via limite)
     * LIMITE       --[IS_SIMILAR]-->    CONTINUIDADE    (ambos descrevem comportamento de funções num ponto)
     *
     * LIMITE       --[IS_CAUSE]-->      CONTINUIDADE
     * LIMITE       --[IS_SIMILAR]-->    CONTINUIDADE
     * FUNCAO       --[IS_BEFORE_OF]-->  CONTINUIDADE
     * CONTINUIDADE --[IS_BEFORE_OF]-->  DERIVADA        (diferenciabilidade exige continuidade)
     *
     * FUNCAO       --[IS_BEFORE_OF]-->  TAXA_VARIACAO
     * NUMERO_REAL  --[IS_BEFORE_OF]-->  TAXA_VARIACAO
     * TAXA_VARIACAO--[IS_SIMILAR]-->    DERIVADA        (taxa média vs. taxa instantânea)
     * TAXA_VARIACAO--[IS_SIMILAR]-->    INTEGRAL        (ambas expressam acumulação/variação de funções)
     *
     * FUNCAO       --[IS_BEFORE_OF]-->  TRIGONOMETRIA
     * ALGEBRA      --[IS_BEFORE_OF]-->  TRIGONOMETRIA
     * NUMERO_REAL  --[IS_BEFORE_OF]-->  TRIGONOMETRIA
     *
     * LIMITE       --[IS_BEFORE_OF]-->  DERIVADA
     * CONTINUIDADE --[IS_BEFORE_OF]-->  DERIVADA
     * FUNCAO       --[IS_BEFORE_OF]-->  DERIVADA
     * TAXA_VARIACAO--[IS_SIMILAR]-->    DERIVADA
     * DERIVADA     --[IS_CAUSE]-->      INTEGRAL        (Teorema Fundamental do Cálculo)
     * DERIVADA     --[IS_SIMILAR]-->    INTEGRAL
     *
     * DERIVADA     --[IS_CAUSE]-->      INTEGRAL
     * DERIVADA     --[IS_SIMILAR]-->    INTEGRAL        (operações inversas do cálculo, ambas via limite)
     * TAXA_VARIACAO--[IS_SIMILAR]-->    INTEGRAL
     * LIMITE       --[IS_BEFORE_OF]-->  INTEGRAL
     * FUNCAO       --[IS_BEFORE_OF]-->  INTEGRAL
     */
    @BeforeEach
    public void prepare(){
        MockitoAnnotations.initMocks(this);

        dummyRelations = new ArrayList<>(){{

            add( new ConceptRelation(dummyConcepts.get(NUMERO_REAL),   ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(ALGEBRA)) );        // 0  NUMERO_REAL_IS_BEFORE_OF_ALGEBRA
            add( new ConceptRelation(dummyConcepts.get(NUMERO_REAL),   ConceptRelationTypesEnum.IS_PART_OF,   dummyConcepts.get(FUNCAO)) );          // 1  NUMERO_REAL_IS_PART_OF_FUNCAO
            add( new ConceptRelation(dummyConcepts.get(ALGEBRA),       ConceptRelationTypesEnum.IS_CAUSE,     dummyConcepts.get(FUNCAO)) );          // 2  ALGEBRA_IS_CAUSE_FUNCAO
            add( new ConceptRelation(dummyConcepts.get(NUMERO_REAL),   ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(SEQUENCIA)) );       // 3  NUMERO_REAL_IS_BEFORE_OF_SEQUENCIA
            add( new ConceptRelation(dummyConcepts.get(SEQUENCIA),     ConceptRelationTypesEnum.IS_PART_OF,   dummyConcepts.get(ALGEBRA)) );         // 4  SEQUENCIA_IS_PART_OF_ALGEBRA
            add( new ConceptRelation(dummyConcepts.get(SEQUENCIA),     ConceptRelationTypesEnum.IS_CAUSE,     dummyConcepts.get(LIMITE)) );          // 5  SEQUENCIA_IS_CAUSE_LIMITE
            add( new ConceptRelation(dummyConcepts.get(ALGEBRA),       ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(FUNCAO)) );          // 6  ALGEBRA_IS_BEFORE_OF_FUNCAO
            add( new ConceptRelation(dummyConcepts.get(FUNCAO),        ConceptRelationTypesEnum.IS_CAUSE,     dummyConcepts.get(TAXA_VARIACAO)) );   // 7  FUNCAO_IS_CAUSE_TAXA_VARIACAO
            add( new ConceptRelation(dummyConcepts.get(FUNCAO),        ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(LIMITE)) );          // 8  FUNCAO_IS_BEFORE_OF_LIMITE
            add( new ConceptRelation(dummyConcepts.get(NUMERO_REAL),   ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(LIMITE)) );          // 9  NUMERO_REAL_IS_BEFORE_OF_LIMITE
            add( new ConceptRelation(dummyConcepts.get(LIMITE),        ConceptRelationTypesEnum.IS_CAUSE,     dummyConcepts.get(CONTINUIDADE)) );    // 10 LIMITE_IS_CAUSE_CONTINUIDADE
            add( new ConceptRelation(dummyConcepts.get(FUNCAO),        ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(CONTINUIDADE)) );    // 11 FUNCAO_IS_BEFORE_OF_CONTINUIDADE
            add( new ConceptRelation(dummyConcepts.get(CONTINUIDADE),  ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(DERIVADA)) );        // 12 CONTINUIDADE_IS_BEFORE_OF_DERIVADA
            add( new ConceptRelation(dummyConcepts.get(FUNCAO),        ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(TAXA_VARIACAO)) );   // 13 FUNCAO_IS_BEFORE_OF_TAXA_VARIACAO
            add( new ConceptRelation(dummyConcepts.get(NUMERO_REAL),   ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(TAXA_VARIACAO)) );   // 14 NUMERO_REAL_IS_BEFORE_OF_TAXA_VARIACAO
            add( new ConceptRelation(dummyConcepts.get(TAXA_VARIACAO), ConceptRelationTypesEnum.IS_SIMILAR,   dummyConcepts.get(DERIVADA)) );        // 15 TAXA_VARIACAO_IS_SIMILAR_DERIVADA
            add( new ConceptRelation(dummyConcepts.get(FUNCAO),        ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(TRIGONOMETRIA)) );   // 16 FUNCAO_IS_BEFORE_OF_TRIGONOMETRIA
            add( new ConceptRelation(dummyConcepts.get(ALGEBRA),       ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(TRIGONOMETRIA)) );   // 17 ALGEBRA_IS_BEFORE_OF_TRIGONOMETRIA
            add( new ConceptRelation(dummyConcepts.get(NUMERO_REAL),   ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(TRIGONOMETRIA)) );   // 18 NUMERO_REAL_IS_BEFORE_OF_TRIGONOMETRIA
            add( new ConceptRelation(dummyConcepts.get(LIMITE),        ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(DERIVADA)) );        // 19 LIMITE_IS_BEFORE_OF_DERIVADA
            add( new ConceptRelation(dummyConcepts.get(FUNCAO),        ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(DERIVADA)) );        // 20 FUNCAO_IS_BEFORE_OF_DERIVADA
            add( new ConceptRelation(dummyConcepts.get(DERIVADA),      ConceptRelationTypesEnum.IS_CAUSE,     dummyConcepts.get(INTEGRAL)) );        // 21 DERIVADA_IS_CAUSE_INTEGRAL
            add( new ConceptRelation(dummyConcepts.get(LIMITE),        ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(INTEGRAL)) );        // 22 LIMITE_IS_BEFORE_OF_INTEGRAL
            add( new ConceptRelation(dummyConcepts.get(FUNCAO),        ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(INTEGRAL)) );        // 23 FUNCAO_IS_BEFORE_OF_INTEGRAL
            add( new ConceptRelation(dummyConcepts.get(SEQUENCIA),     ConceptRelationTypesEnum.IS_SIMILAR,   dummyConcepts.get(FUNCAO)) );          // 24 SEQUENCIA_IS_SIMILAR_FUNCAO
            add( new ConceptRelation(dummyConcepts.get(LIMITE),        ConceptRelationTypesEnum.IS_SIMILAR,   dummyConcepts.get(CONTINUIDADE)) );    // 25 LIMITE_IS_SIMILAR_CONTINUIDADE
            add( new ConceptRelation(dummyConcepts.get(DERIVADA),      ConceptRelationTypesEnum.IS_SIMILAR,   dummyConcepts.get(INTEGRAL)) );        // 26 DERIVADA_IS_SIMILAR_INTEGRAL
            add( new ConceptRelation(dummyConcepts.get(TAXA_VARIACAO), ConceptRelationTypesEnum.IS_SIMILAR,   dummyConcepts.get(INTEGRAL)) );        // 27 TAXA_VARIACAO_IS_SIMILAR_INTEGRAL

        }};

        // Número real — fundamental; aponta para tudo que habilita (owner=origin em todas)
        dummyConcepts.get(NUMERO_REAL).setRelatedConcepts(Set.of(
                dummyRelations.get(NUMERO_REAL_IS_BEFORE_OF_ALGEBRA),
                dummyRelations.get(NUMERO_REAL_IS_PART_OF_FUNCAO),
                dummyRelations.get(NUMERO_REAL_IS_BEFORE_OF_SEQUENCIA),
                dummyRelations.get(NUMERO_REAL_IS_BEFORE_OF_LIMITE),
                dummyRelations.get(NUMERO_REAL_IS_BEFORE_OF_TAXA_VARIACAO),
                dummyRelations.get(NUMERO_REAL_IS_BEFORE_OF_TRIGONOMETRIA)
        ));

        // Álgebra: recebe Número real e Sequência (owner=target);
        //          aponta que causa Função, que é antes de Função e de Trigonometria (owner=origin)
        dummyConcepts.get(ALGEBRA).setRelatedConcepts(Set.of(
                dummyRelations.get(NUMERO_REAL_IS_BEFORE_OF_ALGEBRA),
                dummyRelations.get(SEQUENCIA_IS_PART_OF_ALGEBRA),
                dummyRelations.get(ALGEBRA_IS_CAUSE_FUNCAO),
                dummyRelations.get(ALGEBRA_IS_BEFORE_OF_FUNCAO),
                dummyRelations.get(ALGEBRA_IS_BEFORE_OF_TRIGONOMETRIA)
        ));

        // Sequência: recebe Número real como pré-requisito (owner=target);
        //            aponta que é parte da Álgebra, causa o Limite e é similar a Função (owner=origin)
        dummyConcepts.get(SEQUENCIA).setRelatedConcepts(Set.of(
                dummyRelations.get(NUMERO_REAL_IS_BEFORE_OF_SEQUENCIA),
                dummyRelations.get(SEQUENCIA_IS_PART_OF_ALGEBRA),
                dummyRelations.get(SEQUENCIA_IS_CAUSE_LIMITE),
                dummyRelations.get(SEQUENCIA_IS_SIMILAR_FUNCAO)
        ));

        // Função: recebe Álgebra (cause+before) e Número real como pré-requisitos, Sequência como similar (owner=target);
        //         aponta para Limite, Continuidade, Taxa de variação (cause+before), Trigonometria, Derivada e Integral (owner=origin)
        dummyConcepts.get(FUNCAO).setRelatedConcepts(Set.of(
                dummyRelations.get(ALGEBRA_IS_CAUSE_FUNCAO),
                dummyRelations.get(ALGEBRA_IS_BEFORE_OF_FUNCAO),
                dummyRelations.get(NUMERO_REAL_IS_PART_OF_FUNCAO),
                dummyRelations.get(SEQUENCIA_IS_SIMILAR_FUNCAO),
                dummyRelations.get(FUNCAO_IS_CAUSE_TAXA_VARIACAO),
                dummyRelations.get(FUNCAO_IS_BEFORE_OF_LIMITE),
                dummyRelations.get(FUNCAO_IS_BEFORE_OF_CONTINUIDADE),
                dummyRelations.get(FUNCAO_IS_BEFORE_OF_TAXA_VARIACAO),
                dummyRelations.get(FUNCAO_IS_BEFORE_OF_TRIGONOMETRIA),
                dummyRelations.get(FUNCAO_IS_BEFORE_OF_DERIVADA),
                dummyRelations.get(FUNCAO_IS_BEFORE_OF_INTEGRAL)
        ));

        // Limite: recebe Função, Número real e Sequência como pré-requisitos (owner=target);
        //         aponta que causa Continuidade, é similar a ela, é antes de Derivada e de Integral (owner=origin)
        dummyConcepts.get(LIMITE).setRelatedConcepts(Set.of(
                dummyRelations.get(FUNCAO_IS_BEFORE_OF_LIMITE),
                dummyRelations.get(NUMERO_REAL_IS_BEFORE_OF_LIMITE),
                dummyRelations.get(SEQUENCIA_IS_CAUSE_LIMITE),
                dummyRelations.get(LIMITE_IS_CAUSE_CONTINUIDADE),
                dummyRelations.get(LIMITE_IS_SIMILAR_CONTINUIDADE),
                dummyRelations.get(LIMITE_IS_BEFORE_OF_DERIVADA),
                dummyRelations.get(LIMITE_IS_BEFORE_OF_INTEGRAL)
        ));

        // Continuidade: recebe Limite como causa e similaridade (owner=target);
        //               aponta que é pré-requisito para Derivada (owner=origin)
        dummyConcepts.get(CONTINUIDADE).setRelatedConcepts(Set.of(
                dummyRelations.get(LIMITE_IS_CAUSE_CONTINUIDADE),
                dummyRelations.get(LIMITE_IS_SIMILAR_CONTINUIDADE),
                dummyRelations.get(FUNCAO_IS_BEFORE_OF_CONTINUIDADE),
                dummyRelations.get(CONTINUIDADE_IS_BEFORE_OF_DERIVADA)
        ));

        // Taxa de variação: recebe Função (cause+before) e Número real como pré-requisitos (owner=target);
        //                   aponta similaridade com Derivada e Integral (owner=origin)
        dummyConcepts.get(TAXA_VARIACAO).setRelatedConcepts(Set.of(
                dummyRelations.get(FUNCAO_IS_CAUSE_TAXA_VARIACAO),
                dummyRelations.get(FUNCAO_IS_BEFORE_OF_TAXA_VARIACAO),
                dummyRelations.get(NUMERO_REAL_IS_BEFORE_OF_TAXA_VARIACAO),
                dummyRelations.get(TAXA_VARIACAO_IS_SIMILAR_DERIVADA),
                dummyRelations.get(TAXA_VARIACAO_IS_SIMILAR_INTEGRAL)
        ));

        // Trigonometria: recebe Função e Álgebra como pré-requisitos (owner=target)
        dummyConcepts.get(TRIGONOMETRIA).setRelatedConcepts(Set.of(
                dummyRelations.get(FUNCAO_IS_BEFORE_OF_TRIGONOMETRIA),
                dummyRelations.get(ALGEBRA_IS_BEFORE_OF_TRIGONOMETRIA),
                dummyRelations.get(NUMERO_REAL_IS_BEFORE_OF_TRIGONOMETRIA)
        ));

        // Derivada: recebe Limite, Continuidade e similaridades (owner=target);
        //           aponta que causa Integral e é similar a ela (owner=origin)
        dummyConcepts.get(DERIVADA).setRelatedConcepts(Set.of(
                dummyRelations.get(LIMITE_IS_BEFORE_OF_DERIVADA),
                dummyRelations.get(CONTINUIDADE_IS_BEFORE_OF_DERIVADA),
                dummyRelations.get(FUNCAO_IS_BEFORE_OF_DERIVADA),
                dummyRelations.get(TAXA_VARIACAO_IS_SIMILAR_DERIVADA),
                dummyRelations.get(DERIVADA_IS_CAUSE_INTEGRAL),
                dummyRelations.get(DERIVADA_IS_SIMILAR_INTEGRAL)
        ));

        // Integral: recebe Derivada como causa, similaridade de Derivada e Taxa de variação (owner=target)
        dummyConcepts.get(INTEGRAL).setRelatedConcepts(Set.of(
                dummyRelations.get(DERIVADA_IS_CAUSE_INTEGRAL),
                dummyRelations.get(DERIVADA_IS_SIMILAR_INTEGRAL),
                dummyRelations.get(TAXA_VARIACAO_IS_SIMILAR_INTEGRAL),
                dummyRelations.get(LIMITE_IS_BEFORE_OF_INTEGRAL),
                dummyRelations.get(FUNCAO_IS_BEFORE_OF_INTEGRAL)
        ));

        when(conceptRepo.saveConceptRelation(
                Mockito.any(Concept.class),
                Mockito.any(ConceptRelationTypesEnum.class),
                Mockito.any(Concept.class)
        )).then(i -> {
            ConceptRelation newRelation =  new ConceptRelation(
                i.getArgument(0), i.getArgument(1), i.getArgument(2)
            );
            dummyRelations.add(newRelation);
            return newRelation;
        });

        when(conceptRepo.save(Mockito.any(Concept.class))).then( call -> {
            Concept newConcept = (Concept) call.getArgument(0);
            newConcept.setId( Long.toString( dummyConcepts.size() ) );
            return newConcept;
        });

        when(relationRepo.findByOrigin(
                Mockito.any(Concept.class),
                Mockito.any(ConceptRelationTypesEnum.class)
        )).then( params -> {
           Concept conceptRequired = params.getArgument(0);
           ConceptRelationTypesEnum relationTypeRequired = params.getArgument(1);

            List<ConceptRelation> relationsFound = findRelation(relationTypeRequired, true, conceptRequired);
            return relationsFound;
        });

        when(relationRepo.findByTarget(
                Mockito.any(Concept.class),
                Mockito.any(ConceptRelationTypesEnum.class)
        )).then( params -> {
            Concept conceptRequired = params.getArgument(0);
            ConceptRelationTypesEnum relationTypeRequired = params.getArgument(1);

            List<ConceptRelation> relationsFound = findRelation(relationTypeRequired, false, conceptRequired);
            return relationsFound;
        });

        when(conceptRepo.findByName(Mockito.anyString()))
        .then( calling ->{
            String conceptName = calling.getArgument(0);

            Optional<Concept> conceptFound = dummyConcepts.stream()
                .filter(concept -> concept.getName().equals(conceptName))
                .findFirst();

            Set<ConceptRelation> allRelationsFound = Arrays.stream(ConceptRelationTypesEnum.values()).flatMap(
               conceptRelationType -> {
                   List<ConceptRelation> relations = findRelation(conceptRelationType, true, conceptFound.get());
                   return relations.stream();
               }
            ).collect(Collectors.toSet());

            conceptFound.get().setRelatedConcepts(allRelationsFound);

            return conceptFound;
        });
    }

    private List<ConceptRelation> findRelation(ConceptRelationTypesEnum relationTypeRequired, boolean lookingForOrigin, Concept conceptRequired) {
        ConceptRelationTypesEnum canonicalType = relationTypeRequired.isInverse()
                ? relationTypeRequired.getInverseRelation()
                : relationTypeRequired;

        return dummyRelations.stream().filter(relation -> {
            boolean typeMatches    = relation.getType().equals(canonicalType);
            boolean conceptMatches = lookingForOrigin
                    ? relation.getOrigin().equals(conceptRequired)
                    : relation.getTarget().equals(conceptRequired);
            return typeMatches && conceptMatches;
        }).collect(Collectors.toList());
    }

    @Test
    public void shouldBringConceptsByEmbedding()
    {
        // Vetor representativo da vizinhança semântica de Integral
        Embedding integralEmbedding = Embedding.of(0.91f, 0.35f, 0.12f);

        // DERIVADA, TAXA_VARIACAO e INTEGRAL são similares entre si via IS_SIMILAR
        List<Concept> similarConceptsExpected = List.of(
                dummyConcepts.get(DERIVADA),
                dummyConcepts.get(TAXA_VARIACAO),
                dummyConcepts.get(INTEGRAL)
        );

        when(conceptRepo.find(Mockito.eq(integralEmbedding), Mockito.anyFloat())).thenReturn(similarConceptsExpected);

        List<Concept> similarConcepts = conceptService.findConcepts(integralEmbedding);

        assertIterableEquals(similarConceptsExpected, similarConcepts);
    }

    @Test
    public void shouldBringConceptsByRelationType()
    {
        // ── IS_BEFORE_OF ─────────────────────────────────────────────────────────
        // FUNCAO é o origin com mais relações IS_BEFORE_OF (6 targets)
        assertThat(conceptService.findConcepts(dummyConcepts.get(FUNCAO), ConceptRelationTypesEnum.IS_BEFORE_OF))
                .containsExactlyInAnyOrder(
                        dummyConcepts.get(LIMITE),
                        dummyConcepts.get(CONTINUIDADE),
                        dummyConcepts.get(TAXA_VARIACAO),
                        dummyConcepts.get(TRIGONOMETRIA),
                        dummyConcepts.get(DERIVADA),
                        dummyConcepts.get(INTEGRAL)
                );

        // IS_AFTER_OF (inverso): pesquisar DERIVADA retorna os origins das relações IS_BEFORE_OF onde DERIVADA é target
        assertThat(conceptService.findConcepts(dummyConcepts.get(DERIVADA), ConceptRelationTypesEnum.IS_AFTER_OF))
                .containsExactlyInAnyOrder(
                        dummyConcepts.get(CONTINUIDADE),
                        dummyConcepts.get(LIMITE),
                        dummyConcepts.get(FUNCAO)
                );

        // ── IS_CAUSE ──────────────────────────────────────────────────────────────
        // DERIVADA como origin (todos os concepts têm 1 relação IS_CAUSE; DERIVADA escolhido)
        assertThat(conceptService.findConcepts(dummyConcepts.get(DERIVADA), ConceptRelationTypesEnum.IS_CAUSE))
                .containsExactlyInAnyOrder(
                        dummyConcepts.get(INTEGRAL)
                );

        // IS_CAUSED_BY (inverso): pesquisar INTEGRAL retorna os origins das relações IS_CAUSE onde INTEGRAL é target
        assertThat(conceptService.findConcepts(dummyConcepts.get(INTEGRAL), ConceptRelationTypesEnum.IS_CAUSED_BY))
                .containsExactlyInAnyOrder(
                        dummyConcepts.get(DERIVADA)
                );

        // ── IS_PART_OF ────────────────────────────────────────────────────────────
        // SEQUENCIA como origin (ambos os origins têm 1 relação IS_PART_OF; SEQUENCIA escolhido)
        assertThat(conceptService.findConcepts(dummyConcepts.get(SEQUENCIA), ConceptRelationTypesEnum.IS_PART_OF))
                .containsExactlyInAnyOrder(
                        dummyConcepts.get(ALGEBRA)
                );

        // INCLUDES (inverso): pesquisar ALGEBRA retorna os origins das relações IS_PART_OF onde ALGEBRA é target
        assertThat(conceptService.findConcepts(dummyConcepts.get(ALGEBRA), ConceptRelationTypesEnum.INCLUDES))
                .containsExactlyInAnyOrder(
                        dummyConcepts.get(SEQUENCIA)
                );

        // ── IS_SIMILAR ────────────────────────────────────────────────────────────
        // TAXA_VARIACAO é o origin com mais relações IS_SIMILAR (2 targets)
        // IS_SIMILAR não tem inverso definido no enum, portanto apenas o sentido direto é testado
        assertThat(conceptService.findConcepts(dummyConcepts.get(TAXA_VARIACAO), ConceptRelationTypesEnum.IS_SIMILAR))
                .containsExactlyInAnyOrder(
                        dummyConcepts.get(DERIVADA),
                        dummyConcepts.get(INTEGRAL)
                );
    }

    @Test
    public void shouldCorelateConcepts()
    {
        // ── Conceito existente: ALGEBRA ───────────────────────────────────────────
        // As 3 relações abaixo não existem em dummyRelations:
        //   ALGEBRA → IS_BEFORE_OF → LIMITE    (dummies: ALGEBRA→FUNCAO e ALGEBRA→TRIGONOMETRIA, não LIMITE)
        //   ALGEBRA → IS_BEFORE_OF → DERIVADA  (não existe)
        //   ALGEBRA → COMPLEMENTS  → INTEGRAL  (tipo COMPLEMENTS sem nenhuma entrada nos dummies)
        Concept algebra = dummyConcepts.get(ALGEBRA);

        ConceptRelation expectedBeforeLimite        = new ConceptRelation(algebra, ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(LIMITE));
        ConceptRelation expectedBeforeDerivada      = new ConceptRelation(algebra, ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(DERIVADA));
        ConceptRelation expectedComplementsIntegral = new ConceptRelation(algebra, ConceptRelationTypesEnum.COMPLEMENTS,  dummyConcepts.get(INTEGRAL));

        // ── Três chamadas ao mesmo origin ─────────────────────────────────────────
        conceptService.correlate(algebra, ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(LIMITE));
        conceptService.correlate(algebra, ConceptRelationTypesEnum.IS_BEFORE_OF, dummyConcepts.get(DERIVADA));
        conceptService.correlate(algebra, ConceptRelationTypesEnum.COMPLEMENTS,  dummyConcepts.get(INTEGRAL));

        Optional<Concept> algebraUpdated = conceptRepo.findByName(algebra.getName());

        // ── Verificação ───────────────────────────────────────────────────────────
        // As 3 novas relações devem estar presentes — além das já existentes nos dummies
        assertTrue(algebraUpdated.isPresent());
        assertThat(algebraUpdated.get().getRelatedConcepts())
                .contains(expectedBeforeLimite, expectedBeforeDerivada, expectedComplementsIntegral);
    }

    @Test
    public void shouldSaveConceptsWithRelatedConcepts(){

        // ── Fonte de conteúdo ─────────────────────────────────────────────────────
        // Trechos de um livro de física onde "Aceleração" está definida
        Content cinematicaContent = Content.builder().id("c-fis-1").name("Física Clássica — Cinemática").build();
        Embedding chunkEmbedding = Embedding.of(0.87f, 0.41f, 0.18f);
        Set<ContentChunck> aceleracaoChunks = Set.of(
                ContentChunck.builder().id("ck-fis-1").content(cinematicaContent).start(120).end(250).embedding(chunkEmbedding).build(),
                ContentChunck.builder().id("ck-fis-2").content(cinematicaContent).start(310).end(420).embedding(chunkEmbedding).build()
        );

        // ── Novo conceito: Aceleração ─────────────────────────────────────────────
        Concept aceleracaoConcept = Concept.builder()
                .id(Long.toString(dummyConcepts.size()))
                .name("Aceleração")
                .description("Variação da velocidade em função do tempo; segunda derivada da posição.")
                .chuncks(aceleracaoChunks)
                .build();

        when(conceptRepo.save(Mockito.any(Concept.class))).thenReturn(aceleracaoConcept);

        // ── Salva o conceito ──────────────────────────────────────────────────────
        Concept savedConcept = conceptService.saveConcept(
                "Aceleração",
                "Variação da velocidade em função do tempo; segunda derivada da posição.",
                aceleracaoChunks
        );

        // saveConcept persiste o conceito com os chunks e seus embeddings já preenchidos
        assertNotNull(savedConcept);
        assertEquals("Aceleração", savedConcept.getName());
        assertThat(savedConcept.getChuncks())
                .containsExactlyInAnyOrderElementsOf(aceleracaoChunks);
        assertThat(savedConcept.getChuncks())
                .allMatch(c -> chunkEmbedding.equals(c.getEmbedding()));
    }

}

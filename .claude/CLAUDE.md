# CLAUDE.md — Projeto: Index

> Contexto do domínio do problema e decisões arquiteturais já tomadas.
> Decisões técnicas em aberto estão em `OPTIONS.md`.
> Decisões já tomadas estão em `DECISIONS.md`.

---

## O que é este projeto

Uma ferramenta de estudo pessoal. O usuário insere suas próprias anotações e o sistema as organiza de forma que seja possível:

1. Buscar um conceito trazendo todos os conceitos correlacionados de forma estruturada.
2. Ao encontrar um conceito, visualizar também os conceitos necessários para entendê-lo, em ordem do mais fundamental ao mais específico

---

## Entidades do domínio

### Concept (Conceito)

Unidade atômica de conhecimento. Representa uma ideia, definição ou explicação que o usuário registrou.

Campos:
- `id` — identificador único
- `name` — nome curto do conceito
- `description` — descrição resumida; não armazena o conteúdo original completo
- `chuncks: Set<ContentChunck>` — trechos do conteúdo associados ao conceito
- `relatedConcepts: Set<ConceptRelation>` — relações tipadas com outros conceitos

O embedding semântico não fica no `Concept` — fica em cada `ContentChunck` associado. O conteúdo original nunca é carregado integralmente no modelo. O acesso é sempre parcial, via `ContentChunck` com posições `start`/`end` referenciando um `Content` externo.

### ConceptRelation (Relação entre Conceitos)

Aresta tipada do grafo de conceitos. Expressa a natureza da relação entre dois conceitos.

Campos:
- `origin` — conceito de origem
- `target` — conceito de destino
- `type: ConceptRelationTypesEnum` — tipo da relação

Tipos disponíveis (`ConceptRelationTypesEnum`):

Cada tipo carrega um `relationKey` inteiro. Chaves **positivas** são tipos canônicos (persistidos); chaves **negativas** são os inversos (usados apenas para consulta, nunca armazenados). O método `isInverse()` retorna `true` quando `relationKey < 0`. O método `getInverseRelation()` devolve o tipo oposto multiplicando a chave por `-1`.

| Tipo | Chave | Semântica |
|------|-------|-----------|
| `IS_CAUSE` | +1 | origin implica target como consequência |
| `IS_CAUSED_BY` | −1 | inverso de `IS_CAUSE` |
| `IS_PART_OF` | +2 | origin é parte de target |
| `INCLUDES` | −2 | inverso de `IS_PART_OF` |
| `OPPOSES` | +3 | origin se opõe a target |
| `IS_SIMILAR` | +4 | origin é semanticamente similar a target |
| `COMPLEMENTS` | +5 | origin complementa target |
| `IS_BEFORE_OF` | +6 | origin é pré-requisito de target |
| `IS_AFTER_OF` | −6 | inverso de `IS_BEFORE_OF` |

**Regra de persistência:** somente relações com tipo canônico (não-inverso) são armazenadas. Isso garante que o grafo não contenha arestas redundantes.

**Regra de consulta:** ao receber um tipo inverso como parâmetro de busca, o sistema usa `getInverseRelation()` para obter o tipo canônico correspondente e inverte o papel do conceito consultado — procurando-o como `target` em vez de `origin`. Exemplo:
- `findConcepts(A, IS_PART_OF)` → busca relações `IS_PART_OF` onde **A é origin** (retorna os "todos" dos quais A faz parte)
- `findConcepts(A, INCLUDES)` → busca relações `IS_PART_OF` onde **A é target** (retorna as "partes" que A contém)

### Content (Fonte de Conteúdo)

Referência a um documento ou fonte externa. Não armazena o texto — apenas metadados de identificação.

Campos:
- `id` — identificador único
- `storageId` — identificador no sistema de armazenamento externo
- `name` — nome da fonte
- `description` — descrição opcional
- `author` — autor do conteúdo
- `type` — tipo do documento (ex: PDF, texto)
- `storage` — identificador do backend de armazenamento
- `pagesCount` — número de páginas
- `uploadDate` — data de upload

### ContentChunck (Trecho de Conteúdo)

Fatia posicional de um `Content`. Permite acesso parcial ao conteúdo original.

Campos:
- `id` — identificador único
- `content: Content` — referência à fonte
- `start` — posição inicial do trecho (índice de caractere na página)
- `end` — posição final do trecho (índice de caractere na página)
- `page` — página do conteúdo onde o trecho se encontra
- `embedding: Embedding` — vetor semântico do trecho; gerado e persistido pela camada de infraestrutura

---

## Comportamentos esperados do sistema

- Receber e armazenar anotações do usuário como conceitos
- Permitir ao usuário declarar que um conceito depende de outro
- Buscar conceitos por intenção semântica, não apenas por palavras-chave exatas
- Ao retornar um conceito, trazer junto a cadeia de pré-requisitos em ordem topológica
- A ordem de apresentação deve ir do conceito mais fundamental ao mais próximo do que foi buscado

---

## Propriedades do grafo de dependências

- Dirigido: a dependência tem sentido — A depende de B, não o contrário necessariamente
- Acíclico: um conceito não pode depender de si mesmo direta ou indiretamente
- Sem profundidade máxima definida: a cadeia de pré-requisitos pode ser arbitrariamente longa

---

## Arquitetura

O projeto segue **arquitetura hexagonal organizada por domínio**. Cada módulo representa um domínio, não uma camada técnica. Dentro de cada módulo, os ports descrevem as dependências externas necessárias.

```
index.concept/                          ← domínio "Conceito" (modelos + ports + serviços)
  models/
    Concept.java
    ConceptRelation.java
    Content.java
    ContentChunck.java
    Embedding.java
  constants/
    ConceptRelationTypesEnum.java
  ports/
    repo/
      ConceptRepositoryPort.java         ← port de persistência de conceitos
      ConceptRelationRepositoryPort.java ← port de consulta de relações
      ContentRepositoryPort.java         ← port de persistência de conteúdo
      ContentStoragePort.java            ← port de armazenamento externo do texto
    embedding/
      EmbeddingFactoryPort.java          ← port de geração de vetores
      ChunckingPort.java                 ← port de segmentação semântica
    concept/
      ConceptHelperPort.java             ← port de geração de nome/descrição via IA
  services/
    ConceptService.java                  ← lógica de domínio de conceitos
    ContentService.java                  ← lógica de domínio de conteúdo
    EmbeddingService.java                ← delegação para EmbeddingFactoryPort

index.repo/                             ← adapter de persistência (PostgreSQL + pgvector)
  implements ConceptRepositoryPort
  implements ConceptRelationRepositoryPort
  implements ContentRepositoryPort

index.embedding/                        ← adapter de embedding e armazenamento
  implements EmbeddingFactoryPort
  implements ChunckingPort
  implements ContentStoragePort
  implements ConceptHelperPort

index.server/                           ← camada REST (controllers + DTOs)
```

### Regra de dependência

`index.concept` não conhece nenhum adapter. Os adapters conhecem e dependem de `index.concept`. Nenhuma classe de infraestrutura (JPA, JDBC, HTTP, etc.) entra no módulo `index.concept`.

### ConceptRepositoryPort

| Método | Entrada | Saída | Descrição |
|--------|---------|-------|-----------|
| `findByName` | `String name` | `Optional<Concept>` | Busca por nome exato |
| `findByRelatedConceptNames` | `List<String> names` | `List<Concept>` | Retorna conceitos que possuem *pelo menos* todas as relações informadas |
| `listAllConceptVectors` | — | `Map<String, Embedding>` | Retorna mapa id → embedding de todos os conceitos |
| `find` | `Embedding vector, float tolerance` | `List<Concept>` | Busca por similaridade vetorial; implementação opcional no adapter |
| `save` | `Concept` | `Concept` | Persiste e retorna o conceito salvo |
| `saveConceptRelation` | `Concept concept, ConceptRelationTypesEnum type, Concept target` | `ConceptRelation` | Cria relação tipada entre conceitos; retorna a relação criada |
| `list` | `Integer offset, Integer limit` | `List<Concept>` | Lista conceitos paginados |
| `list` | `Content content, Integer offset, Integer limit` | `List<Concept>` | Lista conceitos associados a um conteúdo, paginados |

### ConceptRelationRepositoryPort

| Método | Entrada | Saída | Descrição |
|--------|---------|-------|-----------|
| `findByOrigin` | `Concept origin, ConceptRelationTypesEnum relationType` | `List<ConceptRelation>` | Relações onde o conceito é origem |
| `findByTarget` | `Concept target, ConceptRelationTypesEnum relationType` | `List<ConceptRelation>` | Relações onde o conceito é destino |
| `findByOriginAndTarget` | `Concept origin, ConceptRelationTypesEnum relationType, Concept target` | `List<ConceptRelation>` | Relação específica entre dois conceitos |

### ContentRepositoryPort

| Método | Entrada | Saída | Descrição |
|--------|---------|-------|-----------|
| `save` | `Content` | `Content` | Persiste e retorna o conteúdo |
| `get` | `String contentId` | `Content` | Recupera conteúdo por id |
| `findAll` | `int offset, int limit` | `List<Content>` | Lista conteúdos paginados |
| `saveChunck` | `ContentChunck` | `ContentChunck` | Persiste um trecho |
| `getContentChunck` | `String contentId, int offset, int limit` | `String` | Recupera texto de um trecho por posição |
| `getContentChunck` | `ContentChunck` | `String` | Recupera texto de um trecho existente |
| `findContentChunckOfContent` | `Content, int offset, int limit` | `List<ContentChunck>` | Lista trechos de um conteúdo paginados |
| `findContentChunckOfContentBetweenPages` | `Content, int startPage, int endPage` | `List<ContentChunck>` | Trechos de um conteúdo em intervalo de páginas |
| `findSimilarContentChuncks` | `Embedding` | `Set<ContentChunck>` | Busca trechos semanticamente similares |
| `relateConcpet` | `List<ContentChunck>, Concept` | `Concept` | Associa trechos a um conceito |
| `getRelatedConcepts` | `Content` | `List<Concept>` | Conceitos associados a um conteúdo |
| `containsConcepts` | `List<Concept>` | `List<Content>` | Conteúdos que contêm os conceitos informados |

### ContentStoragePort

| Método | Entrada | Saída | Descrição |
|--------|---------|-------|-----------|
| `save` | `String content, String id, int page` | `String` | Persiste texto em storage externo; retorna o `storageId` |
| `getContent` | `String storageId, int page, int offset, int limit` | `String` | Recupera fatia `[offset, limit]` de uma página do conteúdo |

### EmbeddingFactoryPort

| Método | Entrada | Saída | Descrição |
|--------|---------|-------|-----------|
| `generate` | `String text` | `Embedding` | Converte texto em vetor semântico |

### ChunckingPort

| Método | Entrada | Saída | Descrição |
|--------|---------|-------|-----------|
| `buildChuncks` | `String content` | `List<ContentChunck>` | Segmenta o texto em trechos semânticos; retorna limites (`start`/`end`) sem vínculo com `Content` |

### ConceptHelperPort

| Método | Entrada | Saída | Descrição |
|--------|---------|-------|-----------|
| `generateConceptDescription` | `List<ContentChunck>` | `String` | Gera descrição do conceito a partir dos trechos associados |
| `generateConceptName` | `List<ContentChunck> similarChuncks` | `String` | Infere nome do conceito a partir de trechos similares |

---

## O que este projeto não é

- Não é um sistema de notas genérico
- Não tem o objetivo de colaboração entre múltiplos usuários (por ora)
- Não é um sistema de flashcards ou quizzes (embora possa evoluir para isso)

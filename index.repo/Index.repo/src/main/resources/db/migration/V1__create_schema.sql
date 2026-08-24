-- ──────────────────────────────────────────────────────────────────────────────
-- V1 — Estrutura inicial do banco de dados
-- Hierarquia: concept → content_chunks → concept_embeddings (1:1)
-- ──────────────────────────────────────────────────────────────────────────────

-- Extensão pgvector: necessária para o tipo vector e os operadores <=> <-> <#>
CREATE EXTENSION IF NOT EXISTS vector;

-- ── Contents ──────────────────────────────────────────────────────────────────
CREATE TABLE contents (
    id          VARCHAR(36)  NOT NULL,
    storage_id  VARCHAR(255) NOT NULL,
    name        VARCHAR(255),
    author      VARCHAR(255),
    type        VARCHAR(20),
    upload_date TIMESTAMP NOT NULL,
    description TEXT,

    CONSTRAINT pk_contents PRIMARY KEY (id)
);

-- ── Concepts ──────────────────────────────────────────────────────────────────
CREATE TABLE concepts (
    id          VARCHAR(36)  NOT NULL,
    name        VARCHAR(255),
    description TEXT,

    CONSTRAINT pk_concepts PRIMARY KEY (id)
);

-- ── Content chunks ────────────────────────────────────────────────────────────
-- Um chunk pertence a um content (origem do texto) e opcionalmente a um concept.
-- concept_id é nullable: o chunk pode existir antes de ser associado a um conceito.
CREATE TABLE content_chunks (
    id         VARCHAR(36) NOT NULL,
    content_id VARCHAR(36) NOT NULL,
    concept_id VARCHAR(36),
    start_pos  INTEGER     NOT NULL,
    end_pos    INTEGER     NOT NULL,
    page       INTEGER     NOT NULL DEFAULT 0,

    CONSTRAINT pk_content_chunks  PRIMARY KEY (id),
    CONSTRAINT fk_chunks_content  FOREIGN KEY (content_id) REFERENCES contents  (id),
    CONSTRAINT fk_chunks_concept  FOREIGN KEY (concept_id) REFERENCES concepts  (id)
);

-- ── Concept embeddings ────────────────────────────────────────────────────────
-- Relação 1:1 com content_chunks — um chunk tem no máximo um embedding.
-- O UNIQUE em chunk_id garante a cardinalidade 1:1 no banco.
-- O tipo vector(384) corresponde ao modelo paraphrase-multilingual-MiniLM-L12-v2.
CREATE TABLE concept_embeddings (
    id        VARCHAR(36)    NOT NULL,
    chunk_id  VARCHAR(36)    NOT NULL,
    embedding vector(384),

    CONSTRAINT pk_concept_embeddings  PRIMARY KEY (id),
    CONSTRAINT fk_embeddings_chunk    FOREIGN KEY (chunk_id) REFERENCES content_chunks (id),
    CONSTRAINT uq_embeddings_chunk    UNIQUE (chunk_id)
);

-- Índice HNSW para busca aproximada por similaridade de cosseno.
-- Habilita o operador <=> em queries de similaridade semântica.
CREATE INDEX idx_embeddings_hnsw
    ON concept_embeddings
    USING hnsw (embedding vector_cosine_ops);

-- ── Concept relations ─────────────────────────────────────────────────────────
-- Arestas tipadas do grafo de conceitos.
-- Somente relações canônicas (chave positiva no enum) são persistidas.
CREATE TABLE concept_relations (
    id        VARCHAR(36) NOT NULL,
    origin_id VARCHAR(36) NOT NULL,
    type      VARCHAR(50) NOT NULL,
    target_id VARCHAR(36) NOT NULL,

    CONSTRAINT pk_concept_relations  PRIMARY KEY (id),
    CONSTRAINT fk_relations_origin   FOREIGN KEY (origin_id) REFERENCES concepts (id),
    CONSTRAINT fk_relations_target   FOREIGN KEY (target_id) REFERENCES concepts (id),
    CONSTRAINT uq_relation           UNIQUE (origin_id, type, target_id)
);

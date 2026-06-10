# OPTIONS.md — Decisões Técnicas em Aberto

> Este arquivo lista as principais decisões de arquitetura ainda não tomadas.
> O objetivo é que cada opção seja analisada no momento certo da implementação,
> com base no contexto real do projeto.
> Quando uma decisão for tomada, mova-a para `DECISIONS.md` com o registro do raciocínio.

---

## 1. Estratégia de busca

**Problema:** como encontrar conceitos relevantes a partir de uma query em linguagem natural.

**Opção A — Busca textual (full-text search)**
Indexa palavras do conteúdo. A busca retorna documentos que contêm os termos exatos ou variações morfológicas da query. Técnicas: TF-IDF, BM25. Disponível nativamente no PostgreSQL via `tsvector`.

Pontos a considerar: simples de implementar, sem dependência externa, mas falha quando o usuário usa palavras diferentes das que estão na anotação.

**Opção B — Busca semântica por embeddings (vetorial)**
O conteúdo e a query são convertidos em vetores numéricos por um modelo de linguagem. A busca retorna os conceitos cujos vetores são mais próximos do vetor da query, independente das palavras usadas.

Pontos a considerar: captura intenção semântica, tolera variações de vocabulário, mas requer um modelo de embedding e um banco capaz de busca vetorial.

**Opção C — Busca híbrida**
Combina as duas abordagens. Útil quando a busca semântica retorna resultados vagos e a busca textual pode refinar por termos específicos.

Pontos a considerar: maior complexidade, mas potencialmente melhor qualidade de resultados.

---

## 2. Modelo de embedding

Aplicável se a Opção B ou C for escolhida na decisão anterior.

**Problema:** qual modelo converter texto em vetores.

**Opção A — Modelo local (sentence-transformers)**
Executa na própria máquina. Não tem custo por uso e não depende de conectividade. Modelos relevantes: `all-MiniLM-L6-v2` (384 dimensões, leve), `paraphrase-multilingual-MiniLM-L12-v2` (melhor suporte a português).

Pontos a considerar: exige Python no ambiente de execução, consumo de CPU/GPU local.

**Opção B — API de embedding externa**
Serviço externo gera os vetores. Exemplos: OpenAI `text-embedding-3-small`, Google `text-embedding-004`.

Pontos a considerar: latência de rede na ingestão, custo por volume, dependência de serviço externo. Google oferece free tier via AI Studio.

---

## 3. Banco de dados

**Problema:** onde e como armazenar conceitos, dependências e (se aplicável) vetores.

**Opção A — PostgreSQL com pgvector (Supabase)**
Banco relacional com extensão vetorial. Resolve armazenamento de conceitos, grafo de dependências e busca vetorial em uma única instância. Supabase oferece hospedagem com free tier.

Pontos a considerar: conveniência de ter tudo num lugar; a busca vetorial em PostgreSQL é menos performática que bancos vetoriais dedicados em escala muito alta, mas adequada para uso pessoal.

**Opção B — Banco relacional separado + banco vetorial dedicado**
Banco relacional (PostgreSQL, SQLite) para conceitos e dependências. Banco vetorial dedicado (Qdrant, Weaviate) para os embeddings e busca semântica. Os dois se comunicam pela camada de aplicação via IDs.

Pontos a considerar: melhor performance de busca vetorial; maior complexidade operacional de manter dois serviços.

**Opção C — SQLite local**
Para uso estritamente local e offline, SQLite resolve a camada relacional sem nenhuma infraestrutura. Pode ser combinado com busca textual simples (sem vetores) se a Opção A da decisão 1 for escolhida.

Pontos a considerar: zero dependência de nuvem, mas sem busca semântica nativa.

---

## 4. Detecção automática de dependências

**Problema:** dependências entre conceitos devem ser declaradas manualmente pelo usuário ou inferidas automaticamente?

**Opção A — Somente manual**
O usuário declara explicitamente que o conceito A depende do conceito B. O sistema não infere nada.

Pontos a considerar: controle total do usuário, sem risco de dependências incorretas, mas exige disciplina na hora de inserir anotações.

**Opção B — Inferência automática por similaridade vetorial**
Após inserir um conceito, o sistema sugere dependências com base nos conceitos mais próximos semanticamente no espaço vetorial. O usuário confirma ou rejeita cada sugestão.

Pontos a considerar: reduz trabalho manual, mas pode sugerir relações incorretas. Requer que a busca vetorial já esteja implementada.

**Opção C — Inferência por LLM**
Dado o conteúdo de um conceito novo e os títulos dos conceitos existentes, um modelo de linguagem sugere quais são pré-requisitos prováveis.

Pontos a considerar: maior qualidade de sugestão, mas introduz custo e latência de chamada a uma API de LLM na ingestão.

---

## 5. Interface do usuário

**Problema:** como o usuário vai interagir com o sistema.

**Opção A — Interface web**
Aplicação React ou similar rodando no browser. Mais adequada para visualização do grafo de dependências.

**Opção B — CLI**
Interface de linha de comando. Mais rápida de construir, adequada para uso técnico.

**Opção C — Integração com ferramenta de notas existente**
Plugin para Obsidian, extensão para Notion, etc. Aproveita o fluxo de anotação já existente do usuário.

Pontos a considerar: reduz atrito de adoção, mas depende da API de terceiros e limita controle sobre a experiência.

---

## 6. Linguagem e runtime da API

**Problema:** qual linguagem usar para o backend.

**Opção A — Python (FastAPI)**
Integração natural com bibliotecas de ML/embeddings (sentence-transformers, numpy). Menos fricção se o serviço de embedding rodar no mesmo processo.

**Opção B — Node.js (Fastify ou Express)**
Mais familiar se o frontend for React. Embedding exigiria chamar um processo Python separado ou usar uma API externa.

---

## Registro de decisões tomadas

Nenhuma decisão tomada ainda. Quando forem tomadas, registrar em `DECISIONS.md`.

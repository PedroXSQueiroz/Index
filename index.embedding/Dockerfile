FROM python:3.12-slim

WORKDIR /app

# Dependências de sistema exigidas pelo PyTorch / sentence-transformers
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    git \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Pré-baixa os dados do NLTK usados pelo SentenceSplitter do LlamaIndex
# (tokenizador de sentenças punkt + tabelas punkt_tab para versões recentes do NLTK)
RUN python -c "\
import nltk; \
nltk.download('punkt', quiet=True); \
nltk.download('punkt_tab', quiet=True)"

# Pré-baixa o modelo multilinguagem para que o container não precise de internet em runtime.
# paraphrase-multilingual-MiniLM-L12-v2: suporta 50+ idiomas, ~470 MB, vetores de 384 dimensões.
RUN python -c "from sentence_transformers import SentenceTransformer; SentenceTransformer('paraphrase-multilingual-MiniLM-L12-v2')"

# Pré-baixa o modelo mT5 fine-tuned para resumo multilinguagem (~1.2 GB)
RUN python -c "\
from transformers import AutoTokenizer, AutoModelForSeq2SeqLM; \
AutoTokenizer.from_pretrained('csebuetnlp/mT5_multilingual_XLSum'); \
AutoModelForSeq2SeqLM.from_pretrained('csebuetnlp/mT5_multilingual_XLSum')"

# Pré-baixa o PTT5 fine-tuned para sumarização em PT-BR, usado na geração de nome
# de conceito (~900 MB). O tokenizer vem do vocabulário português da base da
# Unicamp, não do repositório do modelo.
RUN python -c "\
from transformers import T5Tokenizer, T5ForConditionalGeneration; \
T5Tokenizer.from_pretrained('unicamp-dl/ptt5-base-portuguese-vocab'); \
T5ForConditionalGeneration.from_pretrained('recogna-nlp/ptt5-base-summ-xlsum')"

COPY app.py .

EXPOSE 5000

CMD ["python", "app.py"]

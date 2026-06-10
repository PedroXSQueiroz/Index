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

COPY app.py .

EXPOSE 5000

CMD ["python", "app.py"]

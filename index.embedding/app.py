from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer
from llama_index.core.node_parser import SentenceSplitter
from llama_index.core.schema import Document
from transformers import AutoTokenizer, AutoModelForSeq2SeqLM

app = Flask(__name__)

TEXT_SPLITTER_MODEL_NAME = "paraphrase-multilingual-MiniLM-L12-v2"
text_splitter_model = SentenceTransformer(TEXT_SPLITTER_MODEL_NAME)

splitter = SentenceSplitter(chunk_size=256, chunk_overlap=32)

TEXT_RESUMER_MODEL_NAME = "csebuetnlp/mT5_multilingual_XLSum"
text_resumer_tokenizer  = AutoTokenizer.from_pretrained(TEXT_RESUMER_MODEL_NAME)
text_resumer_model      = AutoModelForSeq2SeqLM.from_pretrained(TEXT_RESUMER_MODEL_NAME)


def parse_text_body(request):
    body = request.get_json(force=True, silent=True)
    if not body or "text" not in body:
        return None, ({"error": "Campo 'text' obrigatório no corpo JSON."}, 400)
    text = body["text"]
    if not isinstance(text, str) or not text.strip():
        return None, ({"error": "O campo 'text' deve ser uma string não vazia."}, 400)
    return text, None


@app.route("/embed", methods=["POST"])
def embed():
    text, err = parse_text_body(request)
    if err:
        return jsonify(err[0]), err[1]

    vector = text_splitter_model.encode(text).tolist()

    return jsonify({"embedding": vector}), 200


@app.route("/chunks", methods=["POST"])
def chunks():
    text, err = parse_text_body(request)
    if err:
        return jsonify(err[0]), err[1]

    nodes = splitter.get_nodes_from_documents([Document(text=text)])

    result = []
    for node in nodes:
        offset = node.start_char_idx if node.start_char_idx is not None else 0
        limit  = node.end_char_idx   if node.end_char_idx   is not None else offset + len(node.get_content())
        result.append({"offset": offset, "limit": limit})

    return jsonify(result), 200


@app.route("/resume", methods=["POST"])
def resume():
    body = request.get_json(force=True, silent=True)

    if not body or "texts" not in body:
        return jsonify({"error": "Campo 'texts' obrigatório no corpo JSON."}), 400

    texts = body["texts"]

    if not isinstance(texts, list) or len(texts) == 0:
        return jsonify({"error": "O campo 'texts' deve ser uma lista não vazia."}), 400

    if not all(isinstance(t, str) and t.strip() for t in texts):
        return jsonify({"error": "Todos os itens de 'texts' devem ser strings não vazias."}), 400

    max_length = body.get("max_length", 256)
    min_length = body.get("min_length", 80)

    compiled_text = " ".join(texts)

    inputs = text_resumer_tokenizer(
        compiled_text,
        return_tensors="pt",
        max_length=1024,
        truncation=True
    )

    summary_ids = text_resumer_model.generate(
        inputs["input_ids"],
        max_length=max_length,
        min_length=min_length,
        num_beams=4,
        early_stopping=True
    )

    summary = text_resumer_tokenizer.decode(summary_ids[0], skip_special_tokens=True)

    return jsonify({"resume": summary}), 200


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok", "model": TEXT_SPLITTER_MODEL_NAME}), 200


print('STARTING EMBEDDING SERVER')
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)

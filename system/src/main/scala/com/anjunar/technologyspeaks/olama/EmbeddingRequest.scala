package com.anjunar.technologyspeaks.olama

case class EmbeddingRequest(model: String = "nomic-embed-text",
                            input: String,
                            options: RequestOptions = RequestOptions())
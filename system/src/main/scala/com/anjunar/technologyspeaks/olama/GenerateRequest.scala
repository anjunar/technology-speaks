package com.anjunar.technologyspeaks.olama

import com.anjunar.technologyspeaks.olama.json.JsonObject

case class GenerateRequest(model: String = "gemma3",
                           prompt: String,
                           suffix: String = null,
                           images: Array[String] = null,
                           format: JsonObject = null,
                           template: String = null,
                           system: String = null,
                           stream: Boolean = false,
                           keepAlive: String = null,
                           raw: Boolean = false)
package com.anjunar.technologyspeaks.olama

import com.anjunar.technologyspeaks.olama.json.{JsonObject, Node}

case class GenerateRequest(model: String = "gemma3",
                           prompt: String,
                           suffix: String = null,
                           images: Array[String] = null,
                           format: Node = null,
                           template: String = null,
                           system: String = null,
                           stream: Boolean = false,
                           keepAlive: String = null,
                           raw: Boolean = false)
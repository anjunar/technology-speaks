package com.anjunar.technologyspeaks.chat

import com.anjunar.technologyspeaks.olama.json.{JsonNode, JsonObject, NodeType}
import com.anjunar.technologyspeaks.olama.{AsyncOLlamaService, ChatMessage, ChatRequest, ChatRole, EmbeddingRequest, GenerateRequest, OLlamaService}
import com.google.common.collect.Lists
import com.typesafe.scalalogging.Logger
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import org.jsoup.Jsoup
import org.jsoup.select.NodeVisitor

import java.util.concurrent.BlockingQueue
import scala.compiletime.uninitialized
import java.util.stream.Stream
import scala.jdk.CollectionConverters.*
import java.util
import scala.collection.immutable.HashSet

@ApplicationScoped
class ChatService {

  val log: Logger = Logger[ChatService]

  @Inject
  var asyncService: AsyncOLlamaService = uninitialized

  @Inject
  var syncService: OLlamaService = uninitialized

  @Transactional
  def chat(text: String, queue: BlockingQueue[String]): Unit = {

    val memoryShortTerm = MemoryEntry.findLatest10()
    val memoryLongTerm = MemoryEntry.findSimilar(syncService.generateEmbeddings(EmbeddingRequest(input = text)).embeddings.head)

    val memory = (memoryShortTerm.asScala.toSet ++ memoryLongTerm.asScala.toSet).toList.sortBy(entry => entry.created)

    val tokenSize = memory.map((entry: MemoryEntry) => entry.tokenSize).sum

    if (tokenSize > 8192) {
      throw new RuntimeException("Token size is too large")
    } else {
      log.info(s"Token size: $tokenSize")
    }

    val session = memory.flatMap(item => Seq(ChatMessage(content = item.question), ChatMessage(content = item.answer, role = ChatRole.ASSISTANT)))

    val request = ChatRequest(messages = session ++ Seq(ChatMessage(content = text)))

    var buffer = ""

    asyncService.chat(request, text => {
      buffer += text
      queue.offer(text)
    })

    val document = Jsoup.parse(buffer)
    val chatResponseText = document.text()

    val systemPrompt =
      """You are responsible for creating memory memoryShortTerm. For every user interaction, summarize the user input and the
        |assistant's response into a single memory entry.
        |Preserve the original language of both the user input and the response.
        |Focus on core meaning and long-term relevance.
        |Respond only with the summary, no extra text or emojis.
        |
        |Format:
        |Summary capturing the essence of the user input and the assistant's response in their original language.""".stripMargin


    val promptText =
      s"""
      |User input: $text
      |Assistant response: $chatResponseText
      |
      |Summary:""".stripMargin

    val response = syncService.generate(GenerateRequest(prompt = promptText, system = systemPrompt))

    val embeddingResponse = syncService.generateEmbeddings(EmbeddingRequest(input = response.response))

    MemoryEntry(response.response, text, buffer, embeddingResponse.embeddings.head)
      .saveOrUpdate()

    queue.offer("!Done!")

  }

}

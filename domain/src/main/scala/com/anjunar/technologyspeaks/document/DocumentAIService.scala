package com.anjunar.technologyspeaks.document

import com.anjunar.technologyspeaks.configuration.jaxrs.ObjectMapperContextResolver
import com.anjunar.technologyspeaks.olama.*
import com.anjunar.technologyspeaks.olama.json.{JsonArray, JsonNode, JsonObject, NodeType}
import com.anjunar.technologyspeaks.semanticspeak.SemanticSpeakService
import com.anjunar.technologyspeaks.shared.editor.*
import com.anjunar.technologyspeaks.shared.hashtag.HashTag
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional

import java.util
import java.util.{Locale, UUID}
import java.util.concurrent.BlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern
import java.util.stream.Collectors
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

import scala.jdk.CollectionConverters.*



@ApplicationScoped
class DocumentAIService {

  @Inject
  var oLlamaService: OLlamaService = uninitialized

  @Inject
  var entityManager: EntityManager = uninitialized

  def create(text: String, blockingQueue: BlockingQueue[String], cancelled : AtomicBoolean) : DocumentAIService.Response = {

    val system = """
                |1.(language) Respond with the ISO 639-1 language code (e.g., 'en', 'de', 'fr') of the input text
                |2.(summary) Please make a very short summary of the input text. Keep the summary in the original language.
                |3.(hashtags) Generate hashtags for the following input text and a short description for each hashtag. Keep the hashtag and short description in the original language
                |4.(chunks) Split the following input text into thematically sections.Each section should cover a separate topic. Keep the title and the content in the original language.
                |
                |{
                |   "language" : "ISO 639-1 language code",
                |   "summary" : "very short summary of the input text. Keep the summary in the original language",
                |   "hashtags" : [
                |       {
                |           "value" : "#Hashtag in original language",
                |           "description" : "A short description in original language"
                |       }
                |   ],
                |   "chunks" : [
                |       {
                |           "title" : "Title in original language",
                |           "content" : "A short summary of the section in original language"
                |       }
                |   ]
                |}
                |""".stripMargin

    val jsonObject = JsonObject(properties = Map(
      "language" -> JsonNode(NodeType.STRING, description = "Respond with the ISO 639-1 language code (e.g., 'en', 'de', 'fr') of the input text"),
      "summary" -> JsonNode(NodeType.STRING, description = "Please make a very short summary of the input text. Keep the summary in the original language."),
      "hashtags" -> JsonArray(
        description = "Generate hashtags for the following input text and a short description for each hashtag. Keep the hashtag and short description in the original language",
        items = JsonObject(properties = Map(
          "value" -> JsonNode(NodeType.STRING, description = "#Hashtag in original language"),
          "description" -> JsonNode(NodeType.STRING, description = "A short description in original language")
      ))),
      "chunks" -> JsonArray(
        description = "Split the following input text into thematically sections.Each section should cover a separate topic. Keep the title and the content in the original language.",
        items = JsonObject(
          properties = Map(
            "title" -> JsonNode(NodeType.STRING, description = "Title in original language"),
            "content" -> JsonNode(NodeType.STRING, description = "A short summary of the section in original language")
      )))
    ))

    val request = GenerateRequest(format = jsonObject, system = system, prompt = text, stream = true)

    var buffer = ""

    oLlamaService.generate(request, line => {
      buffer += line
      blockingQueue.put(line)
    }, cancelled)

    val mapper = ObjectMapperContextResolver.objectMapper
    mapper.readValue(buffer, classOf[DocumentAIService.Response])
  }

  def createSearch(text: String): String = {

    val pattern = Pattern.compile("""(#\w+)""")

    val matcher = pattern.matcher(text)

    var renderedText = text

    while (matcher.find()) {
      val hashTagString = matcher.group(1)

      val hashTag = entityManager.createQuery("select h from HashTag h where h.value = :value", classOf[HashTag])
        .setParameter("value", hashTagString)
        .getSingleResult

      renderedText = renderedText.replace(hashTag.value, hashTag.description)
    }

    val message = s"""Rephrase the following sentence to make it more fluent without changing its meaning.
         |Return only the rephrased text, no additional comments. No thematic expansions.
         |Keep the text in the original language.""".stripMargin

    oLlamaService.generate(GenerateRequest(system = message, prompt = renderedText))
  }



  def createEmbeddings(text: String): Array[Float] = {
    val request = EmbeddingRequest(input = text)

    normalize(oLlamaService.generateEmbeddings(request))
  }

  def normalize(vec: Array[Float]): Array[Float] = {
    val norm = math.sqrt(vec.map(x => x * x).sum).toFloat
    vec.map(_ / norm)
  }

  @Transactional
  def update(id: UUID, blockingQueue: BlockingQueue[String], cancelled : AtomicBoolean): Unit = {

    val document = Document.find(id)
    
    val text = document.editor.toText()

    val response = create(text, blockingQueue, cancelled)

    if (! cancelled.get()) {
      document.language = response.language

      val chunks = response.chunks
      chunks.foreach(chunk => {
        chunk.embedding = createEmbeddings(chunk.title + "\n" + chunk.content)
        chunk.document = document
      })

      val hashTags = response.hashtags
        .map(hashTag => {
          val vector = createEmbeddings(hashTag.description)

          val hashTagsFromDB = entityManager.createQuery("select h from HashTag h where function('similarity', h.value, :value) > 0.8 order by function('similarity', h.value, :value)", classOf[HashTag])
            .setParameter("value", hashTag.value)
            .getResultList

          if (hashTagsFromDB.isEmpty) {
            hashTag.embedding = vector
            hashTag.saveOrUpdate()
            hashTag
          } else {
            hashTagsFromDB.get(0)
          }
        })
        .toList

      blockingQueue.put("\n\nAll Hashtags created\n")

      document.chunks.forEach(chunk => chunk.delete())
      document.chunks.clear()
      document.chunks.addAll(chunks.asJava)

      document.hashTags.clear()
      document.hashTags.addAll(hashTags.asJava)

      document.description = response.summary

      blockingQueue.put("!Done!")
    }

  }

}

object DocumentAIService {

  case class Response(language : Locale, summary : String, chunks : Seq[Chunk], hashtags : Seq[HashTag])

}

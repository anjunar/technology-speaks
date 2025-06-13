package com.anjunar.technologyspeaks.document

import com.anjunar.technologyspeaks.configuration.jaxrs.ObjectMapperContextResolver
import com.anjunar.technologyspeaks.olama.*
import com.anjunar.technologyspeaks.olama.json.*
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
import java.util.regex.Pattern
import java.util.stream.Collectors
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@ApplicationScoped
class DocumentService {

  @Inject
  var oLlamaService: OLlamaService = uninitialized

  @Inject
  var asyncOLlamaService: AsyncOLlamaService = uninitialized

  @Inject
  var semanticSpealService: SemanticSpeakService = uninitialized

  @Inject
  var entityManager: EntityManager = uninitialized

  def createLanguageDetection(text: String): Locale = {

    val response = oLlamaService.chat(ChatRequest(Seq(
      ChatMessage("You are a language detection assistant. Only respond with the ISO 639-1 language code (e.g., 'en', 'de', 'fr') of the input text. Do not provide any explanations or additional content.", ChatRole.SYSTEM),
      ChatMessage(text)
    )))

    Locale.forLanguageTag(response.message.content)
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

    val message = ChatMessage(
      s"""Rephrase the following sentence to make it more fluent without changing its meaning.
         |Return only the rephrased text, no additional comments. No thematic expansions.
         |Keep the text in the original language.
         |
         |Text:
         |
         |$renderedText""".stripMargin)

    val request = ChatRequest(Seq(message))

    val response = oLlamaService.chat(request)

    response.message.content
  }

  def createHashTags(text: String, blockingQueue: BlockingQueue[String]): util.Set[HashTag] = {
    val message = ChatMessage(
      s"""Generate hashtags for the following text and a short description for each hashtag.
         |Keep the text and short description in the original language.
         |Return the hashtags as JSON Array in the following format:
         |
         |[{"value" : "#Hashtag in original language", "description" : "A short description in original language"}]
         |
         |Text:
         |
         |$text""".stripMargin)

    val valueNode = JsonNode(NodeType.STRING)
    val descriptionNode = JsonNode(NodeType.STRING)

    val jsonObject = JsonObject(("value", valueNode), ("description", descriptionNode))

    val jsonArray = JsonArray(jsonObject)

    val request = ChatRequest(jsonArray, Seq(message))

    var buffer = ""

    asyncOLlamaService.chat(request, line => {
      buffer += line
      blockingQueue.put(line)
    })

    val mapper = ObjectMapperContextResolver.objectMapper
    val collectionType = mapper.getTypeFactory.constructCollectionType(classOf[util.Set[HashTag]], classOf[HashTag])
    mapper.readValue(buffer, collectionType)
  }

  def createDescription(text: String, blockingQueue: BlockingQueue[String]): String = {
    val message = ChatMessage(
      s"""Please make a very short summary with the following text.
         |Keep the summary in the original language.
         |Return the summary in JSON Object format.:
         |
         |{"summary": "Here is a very short summary in original language"}
         |
         |Text:
         |
         |$text""".stripMargin)

    val node = JsonNode(NodeType.STRING)

    val jsonObject = JsonObject(("summary", node))

    val request = ChatRequest(jsonObject, Seq(message))

    var buffer = ""

    asyncOLlamaService.chat(request, line => {
      buffer += line
      blockingQueue.put(line)
    })

    val mapper = ObjectMapperContextResolver.objectMapper
    mapper.readValue(buffer, classOf[DocumentService.Summary]).summary
  }

  def createChunks(text: String, blockingQueue: BlockingQueue[String]): util.List[Chunk] = {
    val message = ChatMessage(
      s"""Split the following text into thematically sections.
         |Each section should cover a separate topic.
         |Keep the title and the content in the original language.
         |Return the sections as JSON Array in the following format:
         |
         |[{"title" : "Title in original language", "content" : "A short summary of the section in original language"}]
         |
         |Text:
         |
         |$text""".stripMargin)

    val titleNode = JsonNode(NodeType.STRING)
    val contentNode = JsonNode(NodeType.STRING)

    val jsonObject = JsonObject(("title", titleNode), ("content", contentNode))

    val jsonArray = JsonArray(jsonObject)

    val request = ChatRequest(jsonArray, Seq(message))

    var buffer = ""

    asyncOLlamaService.chat(request, line => {
      buffer += line
      blockingQueue.put(line)
    })

    val mapper = ObjectMapperContextResolver.objectMapper
    val collectionType = mapper.getTypeFactory.constructCollectionType(classOf[util.List[Chunk]], classOf[Chunk])
    mapper.readValue(buffer, collectionType)
  }

  def createEmbeddings(text: String): Array[Float] = {
    val options = RequestOptions(0)

    val request = EmbeddingRequest(text, options)

    normalize(oLlamaService.generateEmbeddings(request).embeddings.head)
    /*
        val textRequest = new TextRequest
        textRequest.texts.add(text)
        semanticSpealService.generateEmbedding(textRequest)
          .embeddings(0)
    */
  }

  def normalize(vec: Array[Float]): Array[Float] = {
    val norm = math.sqrt(vec.map(x => x * x).sum).toFloat
    vec.map(_ / norm)
  }

  def findTop5Embeddings(vector: Array[Float]): java.util.List[Chunk] = {
    val cb = entityManager.getCriteriaBuilder
    val query = cb.createTupleQuery()
    val root = query.from(classOf[Chunk])

    val distanceExpr = cb.function(
      "cosine_distance",
      classOf[java.lang.Double],
      root.get("embedding"),
      cb.parameter(classOf[Array[java.lang.Float]], "embedding")
    )

    query.multiselect(root, distanceExpr)
      .orderBy(cb.asc(distanceExpr))

    entityManager.createQuery(query)
      .setParameter("embedding", vector)
      .setMaxResults(5)
      .getResultStream
      .map(entity => {
        val chunk = entity.get(0, classOf[Chunk])
        chunk
      })
      .toList
  }

  @Transactional
  def update(id: UUID, blockingQueue: BlockingQueue[String]): Unit = {

    val document = Document.find(id)
    
    val text = toText(document.editor.json)

    blockingQueue.put("Start Processing\n")

    document.language = createLanguageDetection(text)

    val chunks = createChunks(text, blockingQueue)
    chunks.forEach(chunk => {
      chunk.embedding = createEmbeddings(chunk.title + "\n" + chunk.content)
      chunk.document = document
    })

    blockingQueue.put("\n\nAll Chunks created\n")

    val hashTags = createHashTags(text, blockingQueue).stream
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
    document.chunks.addAll(chunks)

    document.hashTags.clear()
    document.hashTags.addAll(hashTags)

    document.description = createDescription(text, blockingQueue)

    blockingQueue.put("Done")
  }

  def toText(root: Node): String = root match {
    case node: Table => ""
    case node: ContainerNode => node.children.stream().map(node => toText(node)).collect(Collectors.joining("\n"))
    case node: TextNode => node.value
    case _ => ""
  }

}

object DocumentService {
  class Summary {
      var summary: String = uninitialized
  }
}

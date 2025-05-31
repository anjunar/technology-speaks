package com.anjunar.technologyspeaks.document

import com.anjunar.technologyspeaks.olama.*
import com.anjunar.technologyspeaks.olama.json.{JsonArray, JsonNode, JsonObject, NodeType}
import com.anjunar.technologyspeaks.shared.editor.*
import com.anjunar.technologyspeaks.shared.hashtag.HashTag
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager

import java.util
import java.util.stream.Collectors
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*
import com.anjunar.technologyspeaks.core.*


@ApplicationScoped
class DocumentService {

  @Inject
  var service: OLlamaService = uninitialized

  @Inject
  var entityManager: EntityManager = uninitialized

  def createHashTags(text: String): util.Set[HashTag] = {
    val message = new ChatMessage
    message.role = ChatRole.USER
    message.content = "Bitte erzeuge mir HashTags für den folgenden Text und eine Beschreibung des HashTags. Gib die HashTags als JSON-Liste zurück mit : 'value' und 'description' : " + text

    val valueNode = new JsonNode
    valueNode.nodeType = NodeType.STRING
    val descriptionNode = new JsonNode
    descriptionNode.nodeType = NodeType.STRING

    val jsonObject = new JsonObject
    jsonObject.nodeType = NodeType.OBJECT
    jsonObject.properties.put("value", valueNode)
    jsonObject.properties.put("description", descriptionNode)

    val jsonArray = new JsonArray
    jsonArray.nodeType = NodeType.ARRAY
    jsonArray.items = jsonObject

    val request = new ChatRequest
    request.model = "Llama3.2"
    request.format = jsonArray
    val options = new ChatOptions
    options.temperature = 0
    request.options = options
    request.messages.add(message)

    val response = service.chat(request)

    val mapper = new ObjectMapper
    val collectionType = mapper.getTypeFactory.constructCollectionType(classOf[util.Set[HashTag]], classOf[HashTag])
    mapper.readValue(response.message.content, collectionType)
  }

  def createDescription(text: String): String = {
    val message = new ChatMessage
    message.role = ChatRole.USER
    message.content = "Schreibe mir eine kurze Zusammenfassung vom Text und gib den JSON-String zurück: " + text

    val node = new JsonNode
    node.nodeType = NodeType.STRING

    val request = new ChatRequest
    request.model = "Llama3.2"
    request.format = node
    val options = new ChatOptions
    options.temperature = 0
    request.options = options
    request.messages.add(message)

    val response = service.chat(request)

    response.message.content
  }

  def createChunks(text: String): util.List[Chunk] = {
    val message = new ChatMessage
    message.role = ChatRole.USER
    message.content = "Teile den folgenden Text in thematisch zusammengehörende Abschnitte auf für semantische Suche. Jeder Abschnitt soll ein eigenes Thema enthalten. Gib die Abschnitte als JSON-Liste zurück mit : 'title' und 'content' : " + text

    val titleNode = new JsonNode
    titleNode.nodeType = NodeType.STRING
    val contentNode = new JsonNode
    contentNode.nodeType = NodeType.STRING

    val jsonObject = new JsonObject
    jsonObject.nodeType = NodeType.OBJECT
    jsonObject.properties.put("title", titleNode)
    jsonObject.properties.put("content", contentNode)

    val jsonArray = new JsonArray
    jsonArray.nodeType = NodeType.ARRAY
    jsonArray.items = jsonObject

    val request = new ChatRequest
    request.model = "Llama3.2"
    request.format = jsonArray
    val options = new ChatOptions
    options.temperature = 0
    request.options = options
    request.messages.add(message)

    val response = service.chat(request)

    val mapper = new ObjectMapper
    val collectionType = mapper.getTypeFactory.constructCollectionType(classOf[util.List[Chunk]], classOf[Chunk])
    mapper.readValue(response.message.content, collectionType)
  }

  def createEmbeddings(text: String): Array[Float] = {
    val request = new EmbeddingRequest
    request.input = text
    request.model = "Llama3.2"

    normalize(service.generateEmbeddings(request).embeddings.head)
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
        chunk.distance = entity.get(1, classOf[Double])
        chunk
      })
      .toList
  }

  def update(document: Document): Unit = {

    val text = toText(document.editor.json)

    val chunks = createChunks(text)
    chunks.forEach(chunk => {
      chunk.embedding = createEmbeddings(chunk.title + "\n" + chunk.content)
      chunk.document = document
    })

    val hashTags = createHashTags(text).stream
      .map(hashTag => {
        val vector = createEmbeddings(hashTag.description)

        val hashTagsFromDB = entityManager.createQuery("select h from HashTag h where function('similarity', h.value, :value) > 0.8 order by function('similarity', h.value, :value)", classOf[HashTag])
          .setParameter("value", hashTag.value)
          .getResultList

        if (hashTagsFromDB.isEmpty) {
          hashTag.embedding = vector
          hashTag.persist()
          hashTag
        } else {
          hashTagsFromDB.get(0)
        }
      })
      .toList

    document.chunks.foreach(chunk => chunk.delete())
    document.chunks.clear()
    document.chunks.addAll(chunks)

    document.hashTags.clear()
    document.hashTags.addAll(hashTags)

    document.description = createDescription(text)
  }

  def toText(root: Node): String = root match {
    case node: Table => ""
    case node: ContainerNode => node.children.stream().map(node => toText(node)).collect(Collectors.joining("\n"))
    case node: TextNode => node.value
    case _ => ""
  }

}

package com.anjunar.technologyspeaks.document

import com.anjunar.technologyspeaks.document.json.ChunkNode
import com.anjunar.technologyspeaks.olama.*
import com.anjunar.technologyspeaks.olama.json.{JsonArray, JsonNode, JsonObject, NodeType}
import com.anjunar.technologyspeaks.shared.editor.*
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager

import java.util
import java.util.stream.Collectors
import scala.compiletime.uninitialized

@ApplicationScoped
class DocumentService {

  @Inject
  var service: OLlamaService = uninitialized

  @Inject
  var entityManager: EntityManager = uninitialized

  def createDescription(text: String): String = {
    val message = new ChatMessage
    message.role = ChatRole.USER
    message.content = "Schreibe mir eine kurze Zusammenfassung vom Text und gib den JSON-String zurück: " + text

    val node = new JsonNode
    node.nodeType = NodeType.STRING

    val request = new ChatRequest
    request.model = "Llama3.2"
    request.format = node
    request.messages.add(message)

    val response = service.chat(request)

    response.message.content
  }

  def createChunks(text: String): String = {
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
    request.messages.add(message)

    val response = service.chat(request)

    response.message.content
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

  def findTop5Documents(vector: Array[Float]): java.util.List[Document] = {
    val cb = entityManager.getCriteriaBuilder
    val query = cb.createTupleQuery()
    val root = query.from(classOf[Document])
    val join = root.join("chunks")

    val distanceExpr = cb.avg(cb.function(
      "cosine_distance",
      classOf[java.lang.Double],
      join.get("embedding"),
      cb.parameter(classOf[Array[java.lang.Float]], "embedding")
    ))

    query.multiselect(root, distanceExpr)
      .orderBy(cb.asc(distanceExpr))
      .groupBy(root)

    entityManager.createQuery(query)
      .setParameter("embedding", vector)
      .setMaxResults(5)
      .getResultStream
      .map(entity => {
        val document = entity.get(0, classOf[Document])
        document.score = entity.get(1, classOf[Double])
        document
      })
      .toList
  }

  def update(document: Document): Unit = {

    val text = toText(document.editor.json)

    val jsonString = createChunks(text)

    val mapper = new ObjectMapper
    val collectionType = mapper.getTypeFactory.constructCollectionType(classOf[util.List[ChunkNode]], classOf[ChunkNode])
    val chunkNodes: util.List[ChunkNode] = mapper.readValue(jsonString, collectionType)

    val chunks = chunkNodes.stream()
      .map(chunkNode => {
        val chunk = new Chunk
        chunk.title = chunkNode.title
        chunk.content = chunkNode.content
        chunk.embedding = createEmbeddings(chunkNode.title + "\n" + chunkNode.content)
        chunk.document = document
        chunk
      })
      .toList

    document.chunks.forEach(chunk => chunk.delete())
    document.chunks.clear()
    document.chunks.addAll(chunks)

    document.description = createDescription(text)
  }

  def toText(root: Node): String = root match {
    case node: Table => ""
    case node: ContainerNode => node.children.stream().map(node => toText(node)).collect(Collectors.joining("\n"))
    case node: TextNode => node.value
    case _ => ""
  }

}

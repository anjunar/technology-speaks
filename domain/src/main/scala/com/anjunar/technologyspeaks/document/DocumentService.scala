package com.anjunar.technologyspeaks.document

import com.anjunar.technologyspeaks.document.json.ChunkNode
import com.anjunar.technologyspeaks.olama.json.{JsonArray, JsonNode, JsonObject, NodeType}
import com.anjunar.technologyspeaks.olama.*
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

  def createChunks(text: String): String = {
    val message = new ChatMessage
    message.role = ChatRole.USER
    message.content = "Teile den folgenden Text in thematisch zusammengehörende Abschnitte auf. Jeder Abschnitt soll ein eigenes Thema enthalten.Gib die Abschnitte als JSON-Liste zurück mit : 'title' und 'content' : " + text

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

    service.generateEmbeddings(request).embeddings.head
  }

  def find(embeddings: Array[Float]): util.List[Chunk] = {

    entityManager.createQuery("SELECT d, FUNCTION('cosine_distance', d.embedding, :embedding) as dist FROM Chunk d where FUNCTION('cosine_distance', d.embedding, :embedding) < 2 ORDER BY FUNCTION('cosine_distance', d.embedding, :embedding)", classOf[Array[AnyRef]])
      .setMaxResults(5)
      .setParameter("embedding", embeddings)
      .getResultStream
      .map {
        case Array(doc: Chunk, dist: Double) =>
          doc.distance = dist
          doc
      }
      .collect(Collectors.toList())
  }

  def update(document: Document): Unit = {

    val text = toText(document.editor.ast)

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
  }

  private def toText(root: Node): String = root match {
    case node : Table => ""
    case node : ContainerNode => node.children.stream().map(node => toText(node)).collect(Collectors.joining("\n"))
    case node : TextNode => node.value
  }

}

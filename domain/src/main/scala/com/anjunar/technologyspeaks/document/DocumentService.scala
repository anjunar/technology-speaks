package com.anjunar.technologyspeaks.document

import com.anjunar.technologyspeaks.olama.{EmbeddingRequest, EmbeddingResponse, OLlamaService}
import com.anjunar.technologyspeaks.shared.editor.{AbstractNode, CodeNode, ItemNode, ListNode, ParagraphNode, RootNode, TextNode}
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

import java.util
import java.util.stream.Collectors
import scala.compiletime.uninitialized

@ApplicationScoped
class DocumentService {

  @Inject
  var service : OLlamaService = uninitialized

  def createEmbeddings(text: String): Array[Float] = {
    val request = new EmbeddingRequest
    request.input = text
    request.model = "Llama3.2"

    service.generateEmbeddings(request).embeddings.head
  }

  def find(embeddings : Array[Float]) : util.List[Document] = {

    Document.query("SELECT d FROM Document d where FUNCTION('l2_distance', d.embedding, :embedding) < 10 ORDER BY FUNCTION('l2_distance', d.embedding, :embedding)")
      .setMaxResults(5)
      .setParameter("embedding", embeddings)
      .getResultList
  }

  def count(embeddings: Array[Float]): Long = {
    Document.count("SELECT count(d) FROM Document d where FUNCTION('l2_distance', d.embedding, :embedding) < 10")
      .setParameter("embedding", embeddings)
      .getSingleResult
  }

  def update(document : Document) : Unit = {

    val text = s"${document.title}\n${toText(document.root)}"

    val embeddings = createEmbeddings(text)

    document.embedding = embeddings

  }

  private def toText(node : AbstractNode) : String = node match {
    case node : RootNode => node.children.stream().map(toText).collect(Collectors.joining("\n"))
    case node : CodeNode => node.text
    case node : ListNode => node.children.stream().map(toText).collect(Collectors.joining("\n"))
    case node : ItemNode => node.children.stream().map(toText).collect(Collectors.joining("\t\n"))
    case node : ParagraphNode => node.children.stream().map(toText).collect(Collectors.joining("\n"))
    case node : TextNode => node.text
    case _ => ""
  }

}

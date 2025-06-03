package com.anjunar.technologyspeaks.document

import com.anjunar.technologyspeaks.jaxrs.search.{Context, PredicateProvider}
import com.anjunar.technologyspeaks.shared.hashtag.HashTag
import com.google.common.base.Strings
import com.typesafe.scalalogging.Logger
import jakarta.enterprise.inject.spi.CDI
import jakarta.persistence.criteria.*

import java.lang
import java.net.URLDecoder
import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import scala.util.matching.Regex

class DocumentTextProvider extends PredicateProvider[String, Document] {

  val log: Logger = Logger[DocumentTextProvider]

  override def build(context: Context[String, Document]): Unit = {
    val Context(value, entityManager, builder, predicates, root, query, selection, property, name, parameters) = context


    /*
        if (!Strings.isNullOrEmpty(value)) {
          val documentService = CDI.current().select(classOf[DocumentService]).get()

          val decodedValue = URLDecoder.decode(value, "UTF-8")

          val embeddings = documentService.createEmbeddings(decodedValue)
          searchWithEmbedd(builder, predicates, root, query, selection, parameters, embeddings)
        }
    */

    if (!Strings.isNullOrEmpty(value)) {
      val documentService = CDI.current().select(classOf[DocumentService]).get()

      val hashTagOnlyPattern: Regex = """^(#\w+\s*)+$""".r
      val hashTagPattern: Regex = """(#\w+)""".r

      val decodedValue = URLDecoder.decode(value, "UTF-8")

      decodedValue.trim match {
        case hashTagOnlyPattern(test) =>
          val tags = hashTagPattern.findAllMatchIn(decodedValue).map(_.group(1)).toList
          val hashTagJoin = root.join("hashTags")
          predicates.addOne(hashTagJoin.get("value").in(tags.asJava))
        case _ if hashTagPattern.findFirstIn(decodedValue).isDefined =>
          val tags = hashTagPattern.findAllMatchIn(value).map(_.group(1)).toList
          val cleaned = hashTagPattern.replaceAllIn(value, "").trim

          val searchText = documentService.createSearch(decodedValue)
          log.info(searchText)
          val embeddings = documentService.createEmbeddings(searchText)

          searchWithEmbedd(builder, predicates, root, query, selection, parameters, embeddings)

        case _ =>
          val embeddings = documentService.createEmbeddings(decodedValue)
          searchWithEmbedd(builder, predicates, root, query, selection, parameters, embeddings)
      }
    }
  }

  private def searchWithEmbedd(builder: CriteriaBuilder, predicates: mutable.Buffer[Predicate], root: Root[Document], query: CriteriaQuery[?], selection: mutable.Buffer[Expression[lang.Double]], parameters: mutable.Map[String, Any], embeddings: Array[Float]) = {
    parameters.put("embedding", embeddings)

    val subquery = query.subquery(classOf[lang.Double])
    val chunkRoot = subquery.from(classOf[Chunk])

    val distanceExpr = builder.avg(builder.function(
      "cosine_distance",
      classOf[lang.Double],
      chunkRoot.get("embedding"),
      builder.parameter(classOf[Array[lang.Float]], "embedding")
    ))

    subquery.select(distanceExpr)
      .where(Array(builder.equal(chunkRoot.get("document"), root)) *)

    selection.addOne(subquery)

    val maxDistance = 2d
    val predicate = builder.lessThanOrEqualTo(subquery, maxDistance)
    predicates.addOne(predicate)
  }
}

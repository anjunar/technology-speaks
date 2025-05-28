package com.anjunar.technologyspeaks.document

import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.search.{Context, PredicateProvider}
import com.google.common.base.Strings
import jakarta.enterprise.inject.spi.CDI
import jakarta.persistence.criteria.Expression

import java.lang

class DocumentTextProvider extends PredicateProvider[String, Document ]{

  override def build(context: Context[String, Document]): Unit = {
    val Context(value, entityManager, builder, predicates, root, query, selection, property, name, parameters) = context

    if (! Strings.isNullOrEmpty(value)) {

      val documentService = CDI.current().select(classOf[DocumentService]).get()

      val embeddings = documentService.createEmbeddings(value)

      parameters.put("embedding", embeddings)

      val subquery = query.subquery(classOf[java.lang.Double])
      val chunkRoot = subquery.from(classOf[Chunk])

      val distanceExpr = builder.avg(builder.function(
        "cosine_distance",
        classOf[lang.Double],
        chunkRoot.get("embedding"),
        builder.parameter(classOf[Array[lang.Float]], "embedding")
      ))

      subquery.select(distanceExpr)
        .where(Array(builder.equal(chunkRoot.get("document"), root))*)

      selection.addOne(subquery)

      val maxDistance = 2d
      val predicate = builder.lessThanOrEqualTo(subquery, maxDistance)
      predicates.addOne(predicate)
    }
  }
}

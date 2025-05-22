package com.anjunar.technologyspeaks.jaxrs.search.provider

import com.anjunar.scala.universe.introspector.BeanProperty
import com.anjunar.technologyspeaks.jaxrs.search.{Context, PredicateProvider}
import com.anjunar.technologyspeaks.olama.OLlamaService
import com.google.common.base.Strings
import com.pgvector.PGvector
import jakarta.enterprise.inject.spi.CDI
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Path, Predicate, Root}
import org.postgresql.util.PGobject

import scala.collection.mutable

class GenericSimilarityProvider[E] extends PredicateProvider[String, E] {
  override def build(context : Context[String, E]): Unit = {
    val Context(value, entityManager, builder, predicates, root, query, selection, property, name, parameters) = context

    if (!(value == null) && value.nonEmpty) {

      val segments = name.split("\\.")

      var cursor : Path[?] = root

      segments.foreach(segment => {
        cursor = cursor.get(segment)
      })

      parameters.put(name, context.value)

      val distanceExpr = builder.function(
        "similarity",
        classOf[java.lang.Double],
        cursor,
        builder.parameter(classOf[String], name)
      )

      predicates.addOne(builder.greaterThan(distanceExpr, builder.literal[java.lang.Double](0.3d)))

      selection.addOne(distanceExpr)
    }
  }
}

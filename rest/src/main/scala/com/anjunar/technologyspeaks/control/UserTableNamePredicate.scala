package com.anjunar.technologyspeaks.control

import com.anjunar.scala.universe.introspector.BeanProperty
import com.anjunar.technologyspeaks.jaxrs.search.{Context, PredicateProvider}
import com.anjunar.technologyspeaks.olama.OLlamaService
import com.google.common.base.Strings
import com.pgvector.PGvector
import jakarta.enterprise.inject.spi.CDI
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import org.postgresql.util.PGobject

import scala.collection.mutable

class UserTableNamePredicate extends PredicateProvider[String, User] {
  override def build(context : Context[String, User]): Unit = {
    val Context(value, entityManager, builder, predicates, root, query, selection, property, name, parameters) = context

    if (!(value == null) && value.nonEmpty) {
      val service = CDI.current().select(classOf[UserService]).select().get()

      val vector = service.createEmbeddings(value)

      parameters.put(property.name, vector)

      val distanceExpr = builder.function(
        "cosine_distance",
        classOf[java.lang.Double],
        root.get("fullNameVector"),
        builder.parameter(classOf[Array[java.lang.Float]], property.name)
      )

      predicates.addOne(builder.lessThan(distanceExpr, builder.literal[java.lang.Double](1.0d)))

      selection.addOne(distanceExpr.alias("distance"))
    }
  }
}

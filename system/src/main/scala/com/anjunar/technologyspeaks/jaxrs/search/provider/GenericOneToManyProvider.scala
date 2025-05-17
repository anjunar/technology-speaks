package com.anjunar.technologyspeaks.jaxrs.search.provider

import com.anjunar.technologyspeaks.jaxrs.search.{Context, PredicateProvider}
import com.anjunar.scala.universe.introspector.BeanProperty
import com.anjunar.technologyspeaks.jaxrs.types.IdProvider
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*

import java.util
import java.util.{Set, UUID}
import scala.collection.mutable


class GenericOneToManyProvider[E] extends PredicateProvider[util.Set[UUID], E] {
  override def build(context : Context[util.Set[UUID], E]): Unit = {
    val Context(value, entityManager, builder, predicates, root, query, selection, property, name, parameters) = context

    if (value != null && ! value.isEmpty)
      val join = root.join(property.name)
      predicates.addOne(join.get("id").in(value))
  }
}

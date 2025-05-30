package com.anjunar.technologyspeaks.jaxrs.search.provider

import com.anjunar.technologyspeaks.jaxrs.search.{Context, PredicateProvider}
import com.anjunar.scala.universe.introspector.BeanProperty
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}

import java.time.{LocalDate, LocalDateTime}
import java.util.Objects
import scala.collection.mutable


class GenericDateProvider[E] extends PredicateProvider[LocalDate, E] {
  override def build(context : Context[LocalDate, E]): Unit = {
    val Context(value, entityManager, builder, predicates, root, query, selection, property, name, parameters) = context

    if (Objects.nonNull(value)) {
      val start = value.atStartOfDay
      predicates.addOne(builder.between(root.get(property.name), start, start.plusDays(1)))
    }
  }
}

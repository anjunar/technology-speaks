package com.anjunar.technologyspeaks.jaxrs.search.provider

import com.anjunar.technologyspeaks.jaxrs.search.PredicateProvider
import com.anjunar.technologyspeaks.jaxrs.types.DateTimeDuration
import com.anjunar.scala.universe.introspector.BeanProperty
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}

import java.time.LocalDateTime


class GenericDurationDateTimeProvider[E] extends PredicateProvider[DateTimeDuration, E] {
  override def build(value: DateTimeDuration, entityManager: EntityManager, builder: CriteriaBuilder, root: Root[E], query: CriteriaQuery[?], property: BeanProperty, name: String): Predicate = {
    if (value == null || value.from == null || value.to == null) 
      return builder.conjunction
    val start = value.from
    val end = value.to
    builder.between(root.get(property.name), start, end)
  }
}

package com.anjunar.technologyspeaks.jaxrs.search.provider

import com.anjunar.technologyspeaks.jaxrs.search.PredicateProvider
import com.anjunar.scala.universe.introspector.BeanProperty
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}

import java.time.{LocalDate, LocalDateTime}
import java.util.Objects


class GenericDateProvider[E] extends PredicateProvider[LocalDate, E] {
  override def build(value: LocalDate, entityManager: EntityManager, builder: CriteriaBuilder, root: Root[E], query: CriteriaQuery[?], property: BeanProperty, name: String): Predicate = {
    if (Objects.nonNull(value)) {
      val start = value.atStartOfDay
      return builder.between(root.get(property.name), start, start.plusDays(1))
    }
    builder.conjunction
  }
}

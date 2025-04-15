package com.anjunar.technologyspeaks.jaxrs.search.provider

import com.anjunar.technologyspeaks.jaxrs.search.PredicateProvider
import com.anjunar.technologyspeaks.jaxrs.types.DateDuration
import com.anjunar.scala.universe.introspector.BeanProperty
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}


class GenericDurationDateProvider[E] extends PredicateProvider[DateDuration, E] {
  override def build(value: DateDuration, entityManager: EntityManager, builder: CriteriaBuilder, root: Root[E], query: CriteriaQuery[?], property: BeanProperty, name: String): Predicate = {
    val propertyName : String = if (name.nonEmpty) {
      name
    } else {
      property.name
    }
    if (value != null && value.from != null && value.to != null) 
      return builder.between(root.get(propertyName), value.from, value.to)
    if (value != null && value.from != null && value.to == null) 
      return builder.greaterThan(root.get(propertyName), value.from)
    if (value != null && value.from == null && value.to != null) 
      return builder.lessThan(root.get(propertyName), value.to)
    builder.conjunction
  }
}

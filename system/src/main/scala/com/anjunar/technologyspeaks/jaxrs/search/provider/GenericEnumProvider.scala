package com.anjunar.technologyspeaks.jaxrs.search.provider

import com.anjunar.technologyspeaks.jaxrs.search.PredicateProvider
import com.anjunar.scala.universe.introspector.BeanProperty
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}

import java.util.Objects


class GenericEnumProvider[E] extends PredicateProvider[Enum[_], E] {
  override def build(value: Enum[?], entityManager: EntityManager, builder: CriteriaBuilder, root: Root[E], query: CriteriaQuery[?], property: BeanProperty, name: String): Predicate = {
    if (Objects.nonNull(value)) 
      return builder.equal(root.get(property.name), value)
    builder.conjunction
  }
}

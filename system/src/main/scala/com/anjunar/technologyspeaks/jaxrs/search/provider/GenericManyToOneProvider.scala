package com.anjunar.technologyspeaks.jaxrs.search.provider

import com.anjunar.technologyspeaks.jaxrs.search.PredicateProvider
import com.anjunar.scala.universe.introspector.BeanProperty
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}

import java.util.{Objects, UUID}


class GenericManyToOneProvider[E] extends PredicateProvider[UUID, E] {
  override def build(value: UUID, entityManager: EntityManager, builder: CriteriaBuilder, root: Root[E], query: CriteriaQuery[?], property: BeanProperty, name: String): Predicate = {
    if (Objects.nonNull(value)) 
      return builder.equal(root.get(property.name).get("id"), value)
    builder.conjunction
  }
}

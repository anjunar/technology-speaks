package com.anjunar.technologyspeaks.jaxrs.search

import com.anjunar.scala.universe.introspector.BeanProperty
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}

trait PredicateProvider[V, E] {
  def build(value: V, entityManager: EntityManager, builder: CriteriaBuilder, root: Root[E], query: CriteriaQuery[?], property: BeanProperty, name: String): Predicate
}

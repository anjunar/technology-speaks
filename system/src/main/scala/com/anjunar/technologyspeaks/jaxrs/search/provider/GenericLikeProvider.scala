package com.anjunar.technologyspeaks.jaxrs.search.provider

import com.anjunar.technologyspeaks.jaxrs.search.PredicateProvider
import com.anjunar.scala.universe.introspector.BeanProperty
import com.google.common.base.Strings
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}


class GenericLikeProvider[E] extends PredicateProvider[String, E] {
  override def build(value: String, entityManager: EntityManager, builder: CriteriaBuilder, root: Root[E], query: CriteriaQuery[?], property: BeanProperty, name: String): Predicate = {
    if (Strings.isNullOrEmpty(value)) 
      return builder.conjunction
    builder.like(builder.lower(root.get(property.name)), value.toLowerCase + "%")
  }
}

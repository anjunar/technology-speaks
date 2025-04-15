package com.anjunar.technologyspeaks.jaxrs.search.provider

import com.anjunar.technologyspeaks.jaxrs.search.PredicateProvider
import com.anjunar.scala.universe.introspector.BeanProperty
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*

import java.util
import java.util.{Set, UUID}


class GenericOneToManyProvider[E] extends PredicateProvider[util.Set[UUID], E] {
  override def build(value: util.Set[UUID], entityManager: EntityManager, builder: CriteriaBuilder, root: Root[E], query: CriteriaQuery[?], property: BeanProperty, name: String): Predicate = {
    if (value != null && value.isEmpty) 
      return builder.conjunction
    val join = root.join(property.name)
    join.get("id").in(value)
  }
}

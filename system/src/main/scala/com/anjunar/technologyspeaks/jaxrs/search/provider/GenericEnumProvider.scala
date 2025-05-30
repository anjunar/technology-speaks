package com.anjunar.technologyspeaks.jaxrs.search.provider

import com.anjunar.technologyspeaks.jaxrs.search.{Context, PredicateProvider}
import com.anjunar.scala.universe.introspector.BeanProperty
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}

import java.util.Objects
import scala.collection.mutable


class GenericEnumProvider[E] extends PredicateProvider[Enum[?], E] {
  override def build(context : Context[Enum[?], E]): Unit = {
    val Context(value, entityManager, builder, predicates, root, query, selection, property, name, parameters) = context

    if (Objects.nonNull(value)) 
      return predicates.addOne(builder.equal(root.get(property.name), value))
  }
}

package com.anjunar.technologyspeaks.jaxrs.search.provider

import com.anjunar.technologyspeaks.jaxrs.search.{Context, PredicateProvider}
import com.anjunar.scala.universe.introspector.BeanProperty
import com.google.common.base.Strings
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}

import scala.collection.mutable


class GenericLikeProvider[E] extends PredicateProvider[String, E] {
  override def build(context : Context[String, E]): Unit = {
    val Context(value, entityManager, builder, predicates, root, query, selection, property, name, parameters) = context

    if (! Strings.isNullOrEmpty(value))
      predicates.addOne(builder.like(builder.lower(root.get(property.name)), value.toLowerCase + "%"))

  }
}

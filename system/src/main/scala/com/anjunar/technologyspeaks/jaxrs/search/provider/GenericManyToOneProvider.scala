package com.anjunar.technologyspeaks.jaxrs.search.provider

import com.anjunar.technologyspeaks.jaxrs.search.{Context, PredicateProvider}
import com.anjunar.scala.universe.introspector.BeanProperty
import com.anjunar.technologyspeaks.jaxrs.types.IdProvider
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}

import java.util.{Objects, UUID}
import scala.collection.mutable


class GenericManyToOneProvider[E] extends PredicateProvider[IdProvider, E] {
  override def build(context : Context[IdProvider, E]): Unit = {
    val Context(value, entityManager, builder, predicates, root, query, selection, property, name, parameters) = context

    if (Objects.nonNull(value)) 
      predicates.addOne(builder.equal(root.get(property.name).get("id"), value.id))
  }
}

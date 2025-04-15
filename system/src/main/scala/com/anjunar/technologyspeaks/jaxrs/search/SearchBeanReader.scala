package com.anjunar.technologyspeaks.jaxrs.search

import com.anjunar.scala.universe.introspector.BeanIntrospector
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*

import java.lang.reflect.InvocationTargetException
import java.util
import java.util.function.IntFunction
import java.util.{ArrayList, List, Objects}


object SearchBeanReader {
  
  def read[E](search: AnyRef, entityManager: EntityManager, builder: CriteriaBuilder, root: Root[E], query: CriteriaQuery[?]): Array[Predicate] = {
    val beanModel = BeanIntrospector.createWithType(search.getClass)
    val predicates = new util.ArrayList[Predicate]
    for (property <- beanModel.properties) {
      val restPredicate = property.findDeclaredAnnotation(classOf[RestPredicate])
      if (Objects.nonNull(restPredicate)) {
        val value = property.get(search)
        val predicateClass = restPredicate.value
        val jpaPredicate = predicateClass.getConstructor().newInstance().asInstanceOf[PredicateProvider[AnyRef, E]]
        predicates.add(jpaPredicate.build(value.asInstanceOf[AnyRef], entityManager, builder, root, query, property, restPredicate.property()))
      }
    }
    predicates.toArray((value: Int) => new Array[Predicate](value))
  }

  def order[E](search: AnyRef, entityManager: EntityManager, builder: CriteriaBuilder, root: Root[E], query: CriteriaQuery[?]): Array[Order] = {
    val beanModel = BeanIntrospector.createWithType(search.getClass)
    var predicates : util.List[Order] = new util.ArrayList[Order]
    for (property <- beanModel.properties) {
      val restPredicate = property.findDeclaredAnnotation(classOf[RestSort])
      if (Objects.nonNull(restPredicate)) {
        val value = property.get(search)
        val predicateClass = restPredicate.value
        val jpaPredicate = predicateClass.getConstructor().newInstance().asInstanceOf[RestSortProvider[AnyRef, E]]
        predicates = jpaPredicate.sort(value.asInstanceOf[AnyRef], entityManager, builder, root)
      }
    }
    predicates.toArray((value: Int) => new Array[Order](value))
  }
}

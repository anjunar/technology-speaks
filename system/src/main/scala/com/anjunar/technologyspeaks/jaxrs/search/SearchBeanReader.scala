package com.anjunar.technologyspeaks.jaxrs.search

import com.anjunar.scala.universe.introspector.BeanIntrospector
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*

import java.lang.reflect.InvocationTargetException
import java.util
import java.util.function.IntFunction
import java.util.{ArrayList, List, Objects}
import scala.collection.mutable


object SearchBeanReader {
  
  def read[E](search: AnyRef, entityManager: EntityManager, builder: CriteriaBuilder, root: Root[E], query: CriteriaQuery[?]): (Array[Predicate], mutable.Map[String, Any], mutable.Buffer[Selection[?]]) = {
    val beanModel = BeanIntrospector.createWithType(search.getClass)
    val predicates = new mutable.ListBuffer[Predicate]
    val selection = new mutable.ListBuffer[Selection[_]]
    val parameters = mutable.Map[String, Any]()
    for (property <- beanModel.properties) {
      val restPredicate = property.findDeclaredAnnotation(classOf[RestPredicate])
      if (Objects.nonNull(restPredicate)) {
        val value = property.get(search)
        val predicateClass = restPredicate.value
        val jpaPredicate = predicateClass.getConstructor().newInstance().asInstanceOf[PredicateProvider[AnyRef, E]]
        jpaPredicate.build(Context(value.asInstanceOf[AnyRef], entityManager, builder, predicates, root, query, selection, property, restPredicate.property(), parameters))
      }
    }
    (predicates.toArray, parameters, selection)
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

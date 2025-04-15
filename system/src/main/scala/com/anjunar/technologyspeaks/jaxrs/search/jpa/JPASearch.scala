package com.anjunar.technologyspeaks.jaxrs.search.jpa

import com.anjunar.technologyspeaks.jaxrs.search.SearchBeanReader
import com.anjunar.technologyspeaks.jaxrs.search.jpa.{JPASearchContext, JPASearchContextResult}
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*

import java.util
import java.util.List
import scala.compiletime.uninitialized


@ApplicationScoped
class JPASearch {
  
  @Inject 
  var entityManager: EntityManager = uninitialized

  def searchContext(search: AbstractSearch): JPASearchContext = {
    val context = new JPASearchContext() {
      override def apply[C](entityManager: EntityManager, builder: CriteriaBuilder, query: CriteriaQuery[?], root: Root[C]): JPASearchContextResult = {
        val predicates = SearchBeanReader.read(search, entityManager, builder, root, query)
        val order = SearchBeanReader.order(search, entityManager, builder, root, query)
        new JPASearchContextResult(predicates, order)
      }
    }
    context
  }

  def entities[E](index: Int, limit: Int, entityClass: Class[E], context: JPASearchContext): util.List[E] = {
    val builder = entityManager.getCriteriaBuilder
    val query = builder.createQuery(entityClass)
    val root = query.from(entityClass)
    val apply = context.apply(entityManager, builder, query, root)
    entityManager.createQuery(query.select(root).where(apply.getPredicates*).orderBy(apply.getOrders*))
      .setFirstResult(index)
      .setMaxResults(limit)
      .getResultList
  }

  def count[E](entityClass: Class[E], context: JPASearchContext): Long = {
    val builder = entityManager.getCriteriaBuilder
    val query = builder.createQuery(classOf[java.lang.Long])
    val root = query.from(entityClass)
    val apply = context.apply(entityManager, builder, query, root)
    entityManager.createQuery(query.select(builder.count(root)).where(apply.getPredicates*)).getSingleResult
  }
}

package com.anjunar.technologyspeaks.jaxrs.search.jpa

import com.anjunar.technologyspeaks.jaxrs.search.{Context, PredicateProvider, SearchBeanReader}
import com.anjunar.technologyspeaks.jaxrs.search.jpa.{JPASearchContext, JPASearchContextResult}
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*

import java.util
import java.util.List
import scala.collection.mutable
import scala.compiletime.uninitialized


@ApplicationScoped
class JPASearch {
  
  @Inject 
  var entityManager: EntityManager = uninitialized

  def searchContext[V <: AbstractSearch, E](search: V, predicateProviders : PredicateProvider[V,E]*): JPASearchContext = {
    val context = new JPASearchContext() {
      override def apply[C](entityManager: EntityManager, builder: CriteriaBuilder, query: CriteriaQuery[?], root: Root[C]): JPASearchContextResult = {
        val (predicates, parameters, selection) = SearchBeanReader.read(search, entityManager, builder, root, query)
        val order = SearchBeanReader.order(search, entityManager, builder, root, query)
        predicateProviders.foreach(predicate => predicate.build(Context(search, entityManager, builder, predicates.toBuffer, root.asInstanceOf[Root[E]], query, selection, null, null, parameters)))
        JPASearchContextResult(selection.toArray, predicates, order, parameters)
      }
    }
    context
  }

  def entities[E](index: Int, limit: Int, entityClass: Class[E], context: JPASearchContext): util.List[jakarta.persistence.Tuple] = {
    val builder = entityManager.getCriteriaBuilder
    val query = builder.createTupleQuery()
    val root = query.from(entityClass)
    val apply = context.apply(entityManager, builder, query, root)
    val typedQuery = entityManager.createQuery(query.multiselect((Array(root) ++ apply.selection)*).where(apply.predicates *).orderBy(apply.orders *))
    apply.parameters.foreach((key, value) => typedQuery.setParameter(key, value))
    typedQuery
      .setFirstResult(index)
      .setMaxResults(limit)
      .getResultList
  }

  def count[E](entityClass: Class[E], context: JPASearchContext): Long = {
    val builder = entityManager.getCriteriaBuilder
    val query = builder.createQuery(classOf[java.lang.Long])
    val root = query.from(entityClass)
    val apply = context.apply(entityManager, builder, query, root)
    val typedQuery = entityManager.createQuery(query.select(builder.count(root)).where(apply.predicates *))
    apply.parameters.foreach((key, value) => typedQuery.setParameter(key, value))
    typedQuery.getSingleResult
  }
}

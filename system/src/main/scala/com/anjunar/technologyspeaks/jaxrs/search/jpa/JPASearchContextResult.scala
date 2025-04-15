package com.anjunar.technologyspeaks.jaxrs.search.jpa

import jakarta.persistence.criteria.{Order, Predicate}


class JPASearchContextResult(private val predicates: Array[Predicate], private val orders: Array[Order]) {
  def getPredicates: Array[Predicate] = predicates

  def getOrders: Array[Order] = orders
}

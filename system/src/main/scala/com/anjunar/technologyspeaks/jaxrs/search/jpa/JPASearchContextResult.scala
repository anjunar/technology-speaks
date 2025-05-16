package com.anjunar.technologyspeaks.jaxrs.search.jpa

import jakarta.persistence.criteria.{Order, Predicate, Selection}

import java.util
import scala.collection.mutable


case class JPASearchContextResult(selection : Array[Selection[_]],
                                  predicates: Array[Predicate],
                                  orders: Array[Order],
                                  parameters: mutable.Map[String, Any])

package com.anjunar.technologyspeaks.jaxrs.search.provider

import com.anjunar.technologyspeaks.jaxrs.search.RestSortProvider
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, Order, Root}

import java.util
import java.util.{ArrayList, List}


class GenericSortProvider[E] extends RestSortProvider[util.List[String], E] {
  override def sort(value: util.List[String], entityManager: EntityManager, builder: CriteriaBuilder, root: Root[E]): util.List[Order] = {
    val result = new util.ArrayList[Order]
    if (value == null) return result

    value.forEach(sortExpression => {
      val sortSegment = sortExpression.split(":")
      val cursor = cursor1(root, sortSegment(0))
      val direction = sortSegment(1)
      direction match {
        case "asc" =>
          result.add(builder.asc(cursor))
        case "desc" =>
          result.add(builder.desc(cursor))
      }
    })

    result
  }
}

package com.anjunar.technologyspeaks.jaxrs.search.provider

import com.anjunar.technologyspeaks.jaxrs.search.PredicateProvider
import com.anjunar.technologyspeaks.jaxrs.types.LongIntervall
import com.anjunar.scala.universe.introspector.BeanProperty
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*

import java.util.Objects


class GenericNumberIntervallProvider[E] extends PredicateProvider[LongIntervall, E] {
  override def build(value: LongIntervall, entityManager: EntityManager, builder: CriteriaBuilder, root: Root[E], query: CriteriaQuery[?], property: BeanProperty, name: String): Predicate = {
    if (Objects.nonNull(value)) {
      val path: Path[java.lang.Long] = root.get(property.name)
      return builder.between(path, value.from, value.to)
    }
    null
  }
}

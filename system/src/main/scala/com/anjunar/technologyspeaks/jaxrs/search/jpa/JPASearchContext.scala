package com.anjunar.technologyspeaks.jaxrs.search.jpa

import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Root}


trait JPASearchContext {
  def apply[C](entityManager: EntityManager, builder: CriteriaBuilder, query: CriteriaQuery[_], root: Root[C]): JPASearchContextResult
}

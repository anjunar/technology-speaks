package com.anjunar.technologyspeaks.shared.validators

import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.scala.universe.introspector.BeanIntrospector
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.validation.{ConstraintValidator, ConstraintValidatorContext}

import scala.compiletime.uninitialized

@ApplicationScoped
class UniqueValidator extends ConstraintValidator[Unique, AbstractEntity] {

  var property : String = uninitialized
  
  @Inject
  var entityManager : EntityManager = uninitialized

  override def initialize(constraintAnnotation: Unique): Unit = {
    property = constraintAnnotation.property
  }

  override def isValid(entity: AbstractEntity, constraintValidatorContext: ConstraintValidatorContext): Boolean = {

    val beanModel = BeanIntrospector.createWithType(entity.getClass)
    val beanProperty = beanModel.findProperty(property)
    val value = beanProperty.get(entity)

    val inDatabase = entityManager.find(entity.getClass, entity.id)
    
    val builder = entityManager.getCriteriaBuilder
    val query = builder.createQuery(classOf[AbstractEntity])
    val from = query.from(entity.getClass)
    
    query.select(from).where(Array(builder.equal(from.get(property), value))*)
    val singleResult = entityManager.createQuery(query).getSingleResult

    if (inDatabase == null) {
      singleResult == null
    } else {
      singleResult == inDatabase
    }
  }

}

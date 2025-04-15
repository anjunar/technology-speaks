package com.anjunar.technologyspeaks.jpa

import com.anjunar.scala.mapper.exceptions.{ValidationException, ValidationViolation}
import com.anjunar.scala.universe.TypeResolver
import com.google.common.collect.Sets
import jakarta.enterprise.inject.spi.CDI
import jakarta.persistence.EntityManager
import jakarta.validation.Validator

import java.lang.annotation.Annotation
import java.util
import java.util.stream.Collectors

trait EntityContext {

  def persist(): Unit = {
    CDI.current().getBeanContainer.getEvent.select(new SaveLiteral).fire(this)
    entityManager.persist(this)
  }

  def merge(): Unit = {
    CDI.current().getBeanContainer.getEvent.select(new UpdateLiteral).fire(this)
//    entityManager.merge(this)
  }

  def delete(): Unit = {
    CDI.current().getBeanContainer.getEvent.select(new DeleteLiteral).fire(this)
    entityManager.remove(this)
  }

  def detach(): Unit = {
    entityManager.detach(this)
  }

  def validate(groups: Class[?]*): Unit = {
    val constraintViolation = CDI.current().select(classOf[Validator]).get().validate(this, groups *)

    val validationViolation = constraintViolation.stream()
      .map(violation => {
        val descriptor = violation.getConstraintDescriptor
        val annotation = descriptor.getAnnotation

        val aClass = TypeResolver.resolve(annotation.annotationType)
        val method = aClass.findMethod("property")

        val path = new util.ArrayList[AnyRef]()

        if (method == null)
          violation.getPropertyPath.forEach(node => path.add(node.getName))
        else {
          val invoked = method.invoke(annotation)
          path.add(invoked.asInstanceOf[AnyRef])
        }

        new ValidationViolation(path, violation.getMessage, violation.getRootBeanClass)
      })
      .collect(Collectors.toList)
    
    if (! validationViolation.isEmpty) {
      throw new ValidationException(validationViolation)  
    }
  }

  def entityManager: EntityManager = {
    CDI.current().select(classOf[EntityManager]).get()
  }


}

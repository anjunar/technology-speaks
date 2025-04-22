package com.anjunar.technologyspeaks.jaxrs.json

import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.anjunar.technologyspeaks.security.IdentityContext
import com.anjunar.scala.mapper.Callback
import com.anjunar.scala.mapper.annotations.SecuredOwner
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.UserTransaction
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response.Status

import java.lang.annotation.Annotation
import scala.compiletime.uninitialized

@ApplicationScoped
class JaxRSCallback extends Callback {

  @Inject
  var identityContext : IdentityContext = uninitialized

  @Inject
  var userTransaction : UserTransaction = uninitialized

  override def call(instance: AnyRef, annotations : Array[Annotation]): AnyRef = instance match
    case owner: OwnerProvider =>
      val option = annotations.find(annotation => annotation.annotationType() == classOf[SecuredOwner])
      if (option.isDefined) {
        val principal = identityContext.getPrincipal
        if (principal == null) {
          userTransaction.rollback()
          throw new WebApplicationException(Status.FORBIDDEN)
        } else {
          if owner.owner == principal.user || principal.hasRole("Administrator") then
            owner
          else
            userTransaction.rollback()
            throw new WebApplicationException(Status.FORBIDDEN)
        }
      } else {
        instance
      }
    case _ => instance
}

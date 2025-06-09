package com.anjunar.technologyspeaks.jaxrs.link

import com.anjunar.scala.i18n.I18nResolver
import com.anjunar.scala.mapper.annotations.SecuredOwner
import com.anjunar.scala.schema.model.{Link, LinkType}
import com.anjunar.scala.universe.TypeResolver
import com.anjunar.scala.universe.annotations.Annotated
import com.anjunar.scala.universe.introspector.BeanIntrospector
import com.anjunar.scala.universe.members.ResolvedMethod
import com.anjunar.technologyspeaks.jaxrs.types.*
import com.anjunar.technologyspeaks.security.{IdentityContext, SecurityCredential}
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.security.RolesAllowed
import jakarta.persistence.EntityManager
import jakarta.ws.rs.*
import jakarta.ws.rs.core.{Context, UriBuilder}
import jakarta.ws.rs.ext.{ParamConverter, ParamConverterProvider}

import java.net.URI
import java.util
import java.util.function.BiConsumer
import java.util.{HashMap, Objects}
import scala.compiletime.uninitialized


class JaxRSInvocation(private val method: ResolvedMethod,
                      private val arguments: Array[AnyRef],
                      private val converterProvider: ParamConverterProvider,
                      private val uriBuilder: UriBuilder,
                      private val identityManager: IdentityContext,
                      private val i18nResolver : I18nResolver,
                      private val objectMapper: ObjectMapper) {

  private var rel: String = method.name
  private var httpMethod: String = uninitialized
  private var body: AnyRef = uninitialized
  private var securedOwner = false
  private var owner: OwnerProvider = uninitialized
  private val params = new util.HashMap[String, AnyRef]
  private val classPath: Path = method.owner.findDeclaredAnnotation(classOf[Path])
  private val methodPath: Path = method.findDeclaredAnnotation(classOf[Path])
  private val principal: SecurityCredential = identityManager.getPrincipal

  if (classPath != null && methodPath != null) {
    uriBuilder.path(classPath.value)
    uriBuilder.path(methodPath.value)
  }
  if (classPath == null && methodPath != null)
    uriBuilder.path(methodPath.value)
  if (classPath != null && methodPath == null)
    uriBuilder.path(classPath.value)

  httpMethod = ResourceUtil.httpMethod(method)

  if (Objects.nonNull(arguments))
    for (index <- arguments.indices) {
      val arg = arguments(index)
      val parameter = method.parameters.apply(index)
      readParameter(arg, parameter)
      val beanParam = parameter.findDeclaredAnnotation(classOf[BeanParam])
      val contextParam = parameter.findDeclaredAnnotation(classOf[Context])
      if (beanParam != null && arg != null) {
        val beanModel = BeanIntrospector.create(parameter.parameterType)
        for (beanProperty <- beanModel.properties) {
          readParameter(beanProperty.get(arg).asInstanceOf[AnyRef], beanProperty)
        }
      }
      else if (contextParam != null && arg != null) {

      }
      else {
        if (parameter.findAnnotation(classOf[LinkBody]) != null) {
          body = arg
          val annotation = parameter.findDeclaredAnnotation(classOf[SecuredOwner])
          if (Objects.nonNull(annotation)) securedOwner = true
        }
      }
    }

  // No OP
  /*
          if (httpMethod == null) {
              child = new URLBuilder<>(resolvedMethod.getReturnType().getRawType(), uriBuilder, identity, entityManager, this.converterProvider);
              return child.instance();
          }
  */

  private def readParameter(arg1: AnyRef, parameter: Annotated): Unit = {
    var arg: AnyRef = arg1
    if (arg != null) {
      val converter: ParamConverter[AnyRef] = converterProvider.getConverter(arg.getClass.asInstanceOf[Class[AnyRef]], null, parameter.annotations)
      if (converter != null)
        arg = converter.toString(arg)
    }
    val pathParam = parameter.findDeclaredAnnotation(classOf[PathParam])
    if (pathParam != null)
      arg match
        case value: util.Collection[?]  if ! value.isEmpty => params.put(pathParam.value, value.toArray)
        case _ => params.put(pathParam.value, arg)

    val queryParam = parameter.findDeclaredAnnotation(classOf[QueryParam])
    if (queryParam != null && arg != null)
      arg match
        case value: util.Collection[?] if ! value.isEmpty => uriBuilder.queryParam(queryParam.value, value.toArray*)
        case _ => uriBuilder.queryParam(queryParam.value, arg)

    val matrixParam = parameter.findDeclaredAnnotation(classOf[MatrixParam])
    if (matrixParam != null && arg != null)
      arg match
        case value: util.Collection[?]  if ! value.isEmpty => uriBuilder.matrixParam(matrixParam.value, value.toArray*)
        case _ => uriBuilder.matrixParam(matrixParam.value, arg)
  }

  private def buildMethod: String = {
    if (httpMethod != null) return httpMethod
    //        return child.buildMethod();
    null
  }

  def hasRoles(roles: Array[String]): Boolean = {
    for (role <- roles) {
      if (Objects.nonNull(principal) && principal.hasRole(role))
        return true
    }
    false
  }

  def build(consumer: BiConsumer[String, Link]): Unit = {
    val url = uriBuilder.buildFromMap(params)
    val rolesAllowed = method.findDeclaredAnnotation(classOf[RolesAllowed])
    val linkDescription = method.findDeclaredAnnotation(classOf[LinkDescription])

    val linkType = if linkDescription == null then LinkType.OTHER else linkDescription.linkType()
    val valueDescription = if linkDescription == null then null else i18nResolver.find(linkDescription.value())

    if (Objects.nonNull(owner)) {
      if (identityManager.getPrincipal.equals(owner.owner) || identityManager.getPrincipal.hasRole("Administrator")) {
        proceed(consumer, url, rolesAllowed, linkType, valueDescription)
      }
    } else {
      proceed(consumer, url, rolesAllowed, linkType, valueDescription)
    }
  }

  private def proceed(consumer: BiConsumer[String, Link], url: URI, rolesAllowed: RolesAllowed, linkType: LinkType, valueDescription: String): Unit = {
    if (rolesAllowed == null)
      consumer.accept(rel, new Link(url.toASCIIString, buildMethod, rel, valueDescription, linkType, body))
    else {
      if (hasRoles(rolesAllowed.value)) {
        if (securedOwner) {
          val ownerProvider = body.asInstanceOf[OwnerProvider]
          val credential = identityManager.getPrincipal
          val currentUser = credential.user
          if (credential.hasRole("Administrator") || ownerProvider.owner == currentUser) {
            consumer.accept(rel, new Link(url.toASCIIString, buildMethod, rel, valueDescription, linkType, body))
          }
        } else {
          consumer.accept(rel, new Link(url.toASCIIString, buildMethod, rel, valueDescription, linkType, body))
        }
      }
    }
  }

  def setRel(value: String): Unit = this.rel = value

  def setSecuredOwner(owner: OwnerProvider): Unit = this.owner = owner

  def setRedirect(): Unit = setRel("redirect")

}

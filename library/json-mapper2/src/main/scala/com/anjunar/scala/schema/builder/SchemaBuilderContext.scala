package com.anjunar.scala.schema.builder

import jakarta.inject.Inject
import jakarta.ws.rs.core.{Context, HttpHeaders, Response}

import java.net.URI
import scala.compiletime.uninitialized

trait SchemaBuilderContext {

  @Inject
  var provider: SchemaBuilderProvider = uninitialized

  @Context
  var httpHeaders: HttpHeaders = uninitialized

  def createRedirectResponse = {
    val protocol = httpHeaders.getHeaderString("x-forwarded-protocol")
    val host = httpHeaders.getHeaderString("x-forwarded-host")
    val targetUri = URI.create(s"$protocol://$host")
    Response.seeOther(targetUri).build()
  }

  def forLinks[C](aClass: Class[C], link: (C, LinkContext) => Unit): SchemaBuilder = provider.builder.forLinks(aClass, link)

  def forLinks[C](instance: C, aClass: Class[C], link: (C, LinkContext) => Unit): SchemaBuilder = provider.builder.forLinks(instance, aClass, link)

  def forType[C](aClass: Class[C], builder: EntitySchemaBuilder[C] => Unit): SchemaBuilder = provider.builder.forType(aClass, builder)

  def forInstance[C](instance: C, aClass: Class[C], builder: EntitySchemaBuilder[C] => Unit): SchemaBuilder = provider.builder.forInstance(instance, aClass, builder)

}
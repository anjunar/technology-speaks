package com.anjunar.technologyspeaks.jaxrs.link


import com.anjunar.technologyspeaks.jaxrs.types.OwnerProvider
import com.anjunar.scala.schema.model.Link

import java.util.function.BiConsumer


class WebURLBuilder(private val jaxRSInvocation: JaxRSInvocation) {
  def withRel(value: String): WebURLBuilder = {
    jaxRSInvocation.setRel(value)
    this
  }

  def withRedirect: WebURLBuilder = {
    jaxRSInvocation.setRedirect()
    this
  }
  
  def withSecuredOwner(value : OwnerProvider) : WebURLBuilder = {
    jaxRSInvocation.setSecuredOwner(value)
    this
  }

  def build(consumer: BiConsumer[String, Link]): Unit = {
    jaxRSInvocation.build(consumer)
  }
}

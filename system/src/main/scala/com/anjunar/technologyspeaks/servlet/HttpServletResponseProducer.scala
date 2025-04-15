package com.anjunar.technologyspeaks.servlet

import jakarta.enterprise.context.RequestScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Inject
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}

import scala.compiletime.uninitialized

@RequestScoped
class HttpServletResponseProducer {

  @Inject
  var request: HttpServletRequest = uninitialized

  @Produces
  def produceHttpServletResponse: HttpServletResponse = {
    // Get the current response associated with the request
    request
      .getAttribute("jakarta.servlet.http.HttpServletResponse")
      .asInstanceOf[HttpServletResponse]
  }
}

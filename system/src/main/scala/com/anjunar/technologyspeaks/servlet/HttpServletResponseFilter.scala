package com.anjunar.technologyspeaks.servlet

import jakarta.servlet.{Filter, FilterChain, ServletRequest, ServletResponse}
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}

@WebFilter(filterName = "http-servlet-response", urlPatterns = Array("/service/*"))
class HttpServletResponseFilter extends Filter {

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {
    if (request.isInstanceOf[HttpServletRequest] && response.isInstanceOf[HttpServletResponse]) {
      request.setAttribute("jakarta.servlet.http.HttpServletResponse", response)
    }
    chain.doFilter(request, response)
  }
}

package com.anjunar.technologyspeaks.configuration

import com.google.common.collect.Lists
import jakarta.servlet.*
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest

import java.io.{File, IOException}
import java.net.URL
import java.util


object RewriteServletFilter {
  private val blacklist = Lists.newArrayList("/service")
}

@WebFilter(filterName = "rewriteFilter", urlPatterns = Array("*"), asyncSupported = true)
class RewriteServletFilter extends Filter {

  @throws[ServletException]
  override def init(filterConfig: FilterConfig): Unit = {
    var resource: URL = null
    resource = filterConfig.getServletContext.getResource("/")
    val directory = new File(resource.getFile)
    val list = directory.list
    for (fileName <- list) {
      RewriteServletFilter.blacklist.add("/" + fileName)
    }
  }

  @throws[IOException]
  @throws[ServletException]
  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {

    request match
      case httpServletRequest: HttpServletRequest => {
        val requestURI = httpServletRequest.getRequestURI
        if (RewriteServletFilter.blacklist.stream.noneMatch(requestURI.startsWith))
          request.getServletContext.getRequestDispatcher("/index.html").forward(request, response)
      }

    chain.doFilter(request, response)
  }
}

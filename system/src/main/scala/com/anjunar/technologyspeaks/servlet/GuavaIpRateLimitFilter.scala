package com.anjunar.technologyspeaks.servlet

import com.anjunar.technologyspeaks.servlet.GuavaIpRateLimitFilter.{rateLimiters, requestPerSecond}
import com.google.common.util.concurrent.RateLimiter
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.{Filter, FilterChain, ServletRequest, ServletResponse}

import java.util.concurrent.{ConcurrentHashMap, TimeUnit}

@WebFilter(filterName = "rate-filter", urlPatterns = Array("/service/*"))
class GuavaIpRateLimitFilter extends Filter {

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {

    val remoteAddr = request.getRemoteAddr

    val rateLimiter = rateLimiters.computeIfAbsent(remoteAddr, * => RateLimiter.create(requestPerSecond))

    if (! rateLimiter.tryAcquire(1, TimeUnit.SECONDS)) {
      response.asInstanceOf[HttpServletResponse].sendError(HttpServletResponse.SC_BAD_REQUEST, "Too many requests")
    }

    chain.doFilter(request, response)
  }
}

object GuavaIpRateLimitFilter {
  val rateLimiters = new ConcurrentHashMap[String, RateLimiter]()
  val requestPerSecond = 10
}

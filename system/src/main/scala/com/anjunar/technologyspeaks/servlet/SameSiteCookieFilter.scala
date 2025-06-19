package com.anjunar.technologyspeaks.servlet

import jakarta.servlet._
import jakarta.servlet.annotation._
import jakarta.servlet.http._

@WebFilter(urlPatterns = Array("/*"))
class SameSiteCookieFilter extends Filter {
  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {
    chain.doFilter(request, response)
    response match {
      case httpResp: HttpServletResponse =>
        val headers = httpResp.getHeaders("Set-Cookie").toArray.map(_.toString)
        httpResp.setHeader("Set-Cookie", null)
        headers.foreach { cookie =>
          val updated =
            if (cookie.contains("JSESSIONID")) cookie + "; SameSite=None"
            else cookie
          httpResp.addHeader("Set-Cookie", updated)
        }
      case _ =>
    }
  }
}
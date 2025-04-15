package com.anjunar.technologyspeaks.servlet

import jakarta.annotation.Resource
import jakarta.servlet.*
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.*

import java.io.IOException
import scala.compiletime.uninitialized


@WebFilter(filterName = "transaction", urlPatterns = Array("/service/*"))
class TransactionalFilter extends Filter {

  @Resource
  var userTransaction: UserTransaction = uninitialized

  @throws[IOException]
  @throws[ServletException]
  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {

    val httpServletResponse = response.asInstanceOf[HttpServletResponse]

    userTransaction.begin()

    chain.doFilter(request, response)

    try
      // Is the Transaction Rolled back? Status Code = 6 No Transaction because of Rolled back
      if (userTransaction.getStatus == Status.STATUS_ACTIVE) userTransaction.commit()
    catch
      case e: Exception => {
        response.reset()
        httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
      }

  }
}

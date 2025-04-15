package com.anjunar.technologyspeaks.jaxrs.link

import com.anjunar.scala.universe.members.ResolvedMethod


class MethodInvocation(private val method: ResolvedMethod, private val arguments: Array[AnyRef]) {
  def getMethod: ResolvedMethod = method

  def getArguments: Array[AnyRef] = arguments
}

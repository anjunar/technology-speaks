package com.anjunar.technologyspeaks.jaxrs.link

import java.util
import java.util.List


trait LastInvocationProxy {
  def getInvocation: MethodInvocation

  def getInvocations: util.List[MethodInvocation]
}

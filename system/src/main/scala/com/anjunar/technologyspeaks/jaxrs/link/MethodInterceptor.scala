package com.anjunar.technologyspeaks.jaxrs.link


import com.anjunar.scala.universe.TypeResolver

import java.lang.reflect.{InvocationHandler, Method}
import java.util
import java.util.{ArrayList, List}


class MethodInterceptor(private val last: MethodInterceptor) extends InvocationHandler with LastInvocationProxy {
  
  private var invocation: MethodInvocation = null

  override def getInvocation: MethodInvocation = invocation

  override def getInvocations: util.List[MethodInvocation] = {
    val invocations = new util.ArrayList[MethodInvocation]
    if (last != null) {
      val result = last.getInvocations
      invocations.addAll(result)
    }
    if (invocation != null) 
      invocations.add(invocation)
      
    invocations
  }

  @throws[Throwable]
  override def invoke(proxy: AnyRef, thisMethod: Method, args: Array[AnyRef]): AnyRef = {
    val interfaceOption = proxy.getClass.getInterfaces.headOption
    val javaType = if (interfaceOption.isDefined) {
      TypeResolver.resolve(interfaceOption.get)
    } else {
      TypeResolver.resolve(proxy.getClass.getSuperclass)
    }

    val resolvedMethod = javaType.findMethod(thisMethod.getName, thisMethod.getParameterTypes()*)
    invocation = new MethodInvocation(resolvedMethod, args)
    WebURLBuilderFactory.createProxy(thisMethod.getReturnType, new MethodInterceptor(this))
  }
}

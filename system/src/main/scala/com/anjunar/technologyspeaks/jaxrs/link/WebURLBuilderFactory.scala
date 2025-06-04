package com.anjunar.technologyspeaks.jaxrs.link

import com.anjunar.scala.i18n.I18nResolver
import com.anjunar.technologyspeaks.security.IdentityContext
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.enterprise.inject.spi.CDI
import jakarta.ws.rs.core.UriBuilder
import jakarta.ws.rs.ext.ParamConverterProvider
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.implementation.InvocationHandlerAdapter
import net.bytebuddy.matcher.ElementMatchers

import java.lang.reflect.InvocationHandler
import java.util
import java.util.{ArrayList, Objects}
import scala.collection.mutable


object WebURLBuilderFactory {

  private def paramConverterProvider = CDI.current.select(classOf[ParamConverterProvider]).get

  private def identity = CDI.current.select(classOf[IdentityContext]).get

  private val i18nResolver : I18nResolver = CDI.current().select(classOf[I18nResolver]).get()

  private val proxyCache: ThreadLocal[mutable.Map[Class[?], Class[?]]] = new ThreadLocal[mutable.Map[Class[?], Class[?]]]

  private val objectMapper = new ObjectMapper()

  def linkTo(invocation: AnyRef): WebURLBuilder = {
    val element = invocation.getClass.getField("handler").get(invocation)
    val interceptor = element.asInstanceOf[InvocationHandler]
    val invocations = interceptor.asInstanceOf[MethodInterceptor].getInvocations
    val uriBuilder = UriBuilder.fromPath("/")
    val lastInvocation = invocations.get(invocations.size() - 1)
    new WebURLBuilder(new JaxRSInvocation(lastInvocation.getMethod, lastInvocation.getArguments, paramConverterProvider, uriBuilder, identity, i18nResolver, objectMapper))
  }

  def methodOn[E](aClass: Class[E]): E = createProxy(aClass, new MethodInterceptor(null))

  def createProxy[B](aClass: Class[B], methodHandler: InvocationHandler): B = {

    var hashMap = proxyCache.get()

    if (Objects.isNull(hashMap)) {
      hashMap = new mutable.HashMap[Class[?], Class[?]]()
      proxyCache.set(hashMap)
    }

    val option = hashMap.get(aClass)

    val proxy = option.getOrElse({
      val loaded = new ByteBuddy()
        .subclass(aClass)
        .defineField("handler", classOf[InvocationHandler], Visibility.PUBLIC)
        .method(ElementMatchers.any)
        .intercept(InvocationHandlerAdapter.of(methodHandler))
        .make
        .load(aClass.getClassLoader)
        .getLoaded

      hashMap.put(aClass, loaded)
      proxyCache.set(hashMap)

      loaded
    })

    val instance = proxy.getConstructor().newInstance()
    val field = instance.getClass.getField("handler")
    field.set(instance, methodHandler)

    instance.asInstanceOf[B]
  }
}

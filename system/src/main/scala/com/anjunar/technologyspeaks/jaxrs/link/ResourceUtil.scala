package com.anjunar.technologyspeaks.jaxrs.link

import com.anjunar.scala.universe.members.ResolvedMethod
import jakarta.ws.rs.HttpMethod


object ResourceUtil {
  def httpMethod(resolvedMethod: ResolvedMethod): String = {
    for (annotation <- resolvedMethod.annotations) {
      if (annotation.annotationType.isAnnotationPresent(classOf[HttpMethod]))
        return annotation.annotationType.getAnnotation(classOf[HttpMethod]).value
    }
    null
  }
}
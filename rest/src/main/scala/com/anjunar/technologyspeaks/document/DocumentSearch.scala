package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.jaxrs.search.RestPredicate
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import jakarta.ws.rs.{DefaultValue, QueryParam}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class DocumentSearch extends AbstractSearch {

  @QueryParam("text")
  @RestPredicate(classOf[DocumentTextProvider])
  @PropertyDescriptor(title = "Text", writeable = true)
  var text: String = uninitialized

}
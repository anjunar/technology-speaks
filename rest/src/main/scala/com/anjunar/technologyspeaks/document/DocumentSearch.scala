package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import jakarta.ws.rs.{DefaultValue, QueryParam}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class DocumentSearch extends AbstractSearch {

  @DefaultValue("Hello World")
  @QueryParam("text")
  @BeanProperty
  @Descriptor(title = "Text", writeable = true)
  var text: String = uninitialized

}
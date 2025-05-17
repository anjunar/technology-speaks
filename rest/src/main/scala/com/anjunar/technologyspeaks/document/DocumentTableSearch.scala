package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import jakarta.ws.rs.{DefaultValue, QueryParam}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class DocumentTableSearch extends AbstractSearch {

  @BeanProperty
  @QueryParam("text")
  @Descriptor(title = "Text", writeable = true)
  var text: String = uninitialized

}
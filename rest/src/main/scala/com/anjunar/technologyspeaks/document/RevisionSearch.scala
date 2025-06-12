package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import jakarta.ws.rs.PathParam

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class RevisionSearch extends AbstractSearch {

  @PropertyDescriptor(title = "Document")
  @PathParam("id")
  @BeanProperty  
  var document : Document = uninitialized

}

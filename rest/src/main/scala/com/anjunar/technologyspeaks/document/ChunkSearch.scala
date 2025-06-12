package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.{PropertyDescriptor, Filter, IgnoreFilter}
import com.anjunar.technologyspeaks.jaxrs.search.RestPredicate
import com.anjunar.technologyspeaks.jaxrs.search.provider.GenericManyToOneProvider
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import jakarta.ws.rs.QueryParam

import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class ChunkSearch extends AbstractSearch {

  @PropertyDescriptor(title = "Document", writeable = true)
  @RestPredicate(classOf[GenericManyToOneProvider[?]])
  @QueryParam("document")
  var document: Document = uninitialized

}

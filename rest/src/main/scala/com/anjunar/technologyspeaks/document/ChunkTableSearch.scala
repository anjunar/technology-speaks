package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.{Filter, IgnoreFilter}
import com.anjunar.technologyspeaks.jaxrs.search.RestPredicate
import com.anjunar.technologyspeaks.jaxrs.search.provider.GenericManyToOneProvider
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import jakarta.ws.rs.QueryParam

import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class ChunkTableSearch extends AbstractSearch {

  @RestPredicate(classOf[GenericManyToOneProvider[?]])
  @QueryParam("document")
  @BeanProperty
  var document: UUID = uninitialized

}

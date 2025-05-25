package com.anjunar.technologyspeaks.timeline

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.control.User
import com.anjunar.technologyspeaks.jaxrs.search.RestPredicate
import com.anjunar.technologyspeaks.jaxrs.search.provider.GenericManyToOneProvider
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import jakarta.ws.rs.QueryParam

import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class PostSearch extends AbstractSearch {

  @Descriptor(title = "User", writeable = true, widget = "lazy-select")
  @RestPredicate(classOf[GenericManyToOneProvider[?]])
  @QueryParam("user")
  @BeanProperty
  var user: User = uninitialized

}

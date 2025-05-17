package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jaxrs.search.{RestPredicate, RestSort}
import com.anjunar.technologyspeaks.jaxrs.search.provider.{GenericIdProvider, GenericNameProvider, GenericSortProvider}
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import jakarta.ws.rs.QueryParam

import java.util
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class RoleTableSearch extends AbstractSearch {

  @Descriptor(title = "Name", writeable = true)
  @RestPredicate(classOf[GenericNameProvider[?]])
  @QueryParam("name")
  @BeanProperty
  var name: String = uninitialized

  @Descriptor(title = "Description", writeable = true)
  @RestPredicate(classOf[GenericNameProvider[?]])
  @QueryParam("description")
  @BeanProperty
  var description: String = uninitialized

}


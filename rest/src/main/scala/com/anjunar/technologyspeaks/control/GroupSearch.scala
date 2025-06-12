package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.jaxrs.search.{RestPredicate, RestSort}
import com.anjunar.technologyspeaks.jaxrs.search.provider.{GenericIdProvider, GenericNameProvider, GenericSortProvider}
import com.anjunar.technologyspeaks.jaxrs.types.{AbstractSearch, Sort}
import jakarta.ws.rs.QueryParam

import java.util
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class GroupSearch extends AbstractSearch {

  @PropertyDescriptor(title = "Name", writeable = true)
  @RestPredicate(classOf[GenericNameProvider[?]])
  @QueryParam("name")
  var name: String = uninitialized

}
package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.control.Role
import com.anjunar.technologyspeaks.jaxrs.search.RestPredicate
import com.anjunar.technologyspeaks.jaxrs.search.provider.{GenericManyToManyProvider, GenericNameProvider}
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import jakarta.ws.rs.QueryParam

import java.util
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class CredentialSearch extends AbstractSearch {

  @PropertyDescriptor(title = "Display Name", writeable = true)
  @RestPredicate(classOf[GenericNameProvider[?]])
  @QueryParam("displayName")
  var displayName: String = uninitialized

  @PropertyDescriptor(title = "Roles", writeable = true)
  @RestPredicate(classOf[GenericManyToManyProvider[?]])
  @QueryParam("roles")
  val roles: util.Set[Role] = new util.HashSet[Role]()

}

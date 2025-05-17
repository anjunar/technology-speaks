package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.control.Role
import com.anjunar.technologyspeaks.jaxrs.search.RestPredicate
import com.anjunar.technologyspeaks.jaxrs.search.provider.{GenericManyToManyProvider, GenericNameProvider}
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import jakarta.ws.rs.QueryParam

import java.util
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class CredentialTableSearch extends AbstractSearch {

  @Descriptor(title = "Display Name", writeable = true)
  @RestPredicate(classOf[GenericNameProvider[?]])
  @QueryParam("displayName")
  @BeanProperty
  var displayName: String = uninitialized

  @Descriptor(title = "Roles", writeable = true, schemaType = classOf[Role])
  @RestPredicate(classOf[GenericManyToManyProvider[?]])
  @QueryParam("roles")
  @BeanProperty
  val roles: util.Set[UUID] = new util.HashSet[UUID]()

}

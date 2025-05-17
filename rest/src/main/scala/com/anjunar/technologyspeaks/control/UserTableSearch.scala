package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jaxrs.search.RestPredicate
import com.anjunar.technologyspeaks.jaxrs.search.provider.{GenericDurationDateProvider, GenericNameProvider}
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import jakarta.ws.rs.QueryParam

import java.time.LocalDate
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class UserTableSearch extends AbstractSearch {

  @Descriptor(title = "Email", writeable = true)
  @RestPredicate(classOf[GenericNameProvider[?]])
  @QueryParam("email")
  @BeanProperty
  private var email: String = uninitialized

  @Descriptor(title = "Name", writeable = true)
  @RestPredicate(classOf[UserTableNamePredicate])
  @QueryParam("nickName")
  @BeanProperty
  private var nickName: String = uninitialized

  @Descriptor(title = "Birthdate", writeable = true)
  @RestPredicate(classOf[GenericDurationDateProvider[?]])
  @QueryParam("birthDate")
  @BeanProperty
  private var birthDate: LocalDate = uninitialized

}
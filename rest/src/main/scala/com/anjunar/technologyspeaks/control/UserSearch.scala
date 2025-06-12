package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.jaxrs.search.RestPredicate
import com.anjunar.technologyspeaks.jaxrs.search.provider.{GenericDurationDateProvider, GenericNameProvider, GenericSimilarityProvider}
import com.anjunar.technologyspeaks.jaxrs.types.{AbstractSearch, DateDuration}
import jakarta.ws.rs.QueryParam

import java.time.LocalDate
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class UserSearch extends AbstractSearch {

  @PropertyDescriptor(title = "Email", writeable = true)
  @RestPredicate(classOf[GenericNameProvider[?]])
  @QueryParam("email")
  var email: String = uninitialized

  @PropertyDescriptor(title = "Nick Name", writeable = true)
  @RestPredicate(value = classOf[GenericSimilarityProvider[?]], property = "nickName")
  @QueryParam("nickName")
  var nickName: String = uninitialized

  @PropertyDescriptor(title = "First Name", writeable = true)
  @RestPredicate(value = classOf[UserNameProvider], property = "firstName")
  @QueryParam("firstName")
  var firstName: String = uninitialized

  @PropertyDescriptor(title = "Last Name", writeable = true)
  @RestPredicate(value = classOf[UserNameProvider], property = "lastName")
  @QueryParam("lastName")
  var lastName: String = uninitialized

  @PropertyDescriptor(title = "Birthdate", writeable = true)
  @RestPredicate(classOf[GenericDurationDateProvider[?]])
  @QueryParam("birthDate")
  var birthDate: DateDuration = uninitialized

}
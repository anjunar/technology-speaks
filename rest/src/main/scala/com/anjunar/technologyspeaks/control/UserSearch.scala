package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jaxrs.search.RestPredicate
import com.anjunar.technologyspeaks.jaxrs.search.provider.{GenericDurationDateProvider, GenericNameProvider, GenericSimilarityProvider}
import com.anjunar.technologyspeaks.jaxrs.types.{AbstractSearch, DateDuration}
import jakarta.ws.rs.QueryParam

import java.time.LocalDate
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class UserSearch extends AbstractSearch {

  @Descriptor(title = "Email", writeable = true)
  @RestPredicate(classOf[GenericNameProvider[?]])
  @QueryParam("email")
  @BeanProperty
  var email: String = uninitialized

  @Descriptor(title = "Nick Name", writeable = true)
  @RestPredicate(value = classOf[GenericSimilarityProvider[?]], property = "nickName")
  @QueryParam("nickName")
  @BeanProperty
  var nickName: String = uninitialized

  @Descriptor(title = "First Name", writeable = true)
  @RestPredicate(value = classOf[UserNameProvider], property = "firstName")
  @QueryParam("firstName")
  @BeanProperty
  var firstName: String = uninitialized

  @Descriptor(title = "Last Name", writeable = true)
  @RestPredicate(value = classOf[UserNameProvider], property = "lastName")
  @QueryParam("lastName")
  @BeanProperty
  var lastName: String = uninitialized

  @Descriptor(title = "Birthdate", writeable = true)
  @RestPredicate(classOf[GenericDurationDateProvider[?]])
  @QueryParam("birthDate")
  @BeanProperty
  var birthDate: DateDuration = uninitialized

}
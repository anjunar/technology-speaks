package com.anjunar.technologyspeaks

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.control.User

import java.util
import scala.beans.BeanProperty

class Application(_user: User) {

  @BeanProperty
  @Descriptor(title = "User")
  val user: User = _user

  def this() = this(null)

}

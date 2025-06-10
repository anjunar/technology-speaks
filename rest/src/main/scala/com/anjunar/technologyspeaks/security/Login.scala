package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.Descriptor
import org.jboss.resteasy.annotations.jaxrs.FormParam

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Login {

  @BeanProperty
  @Descriptor(title = "Email", widget = "email")
  @FormParam("username")  
  var username: String = uninitialized

  @Descriptor(title = "Password", widget = "password")
  @BeanProperty
  @FormParam("password")
  var password : String = uninitialized
  
  @BeanProperty
  @Descriptor(title = "Device Name", widget = "text")
  var displayName : String = uninitialized
  
}

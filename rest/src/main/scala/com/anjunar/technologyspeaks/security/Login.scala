package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import org.jboss.resteasy.annotations.jaxrs.FormParam

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Login {

  @PropertyDescriptor(title = "Email", widget = "email")
  @FormParam("username")  
  var username: String = uninitialized

  @PropertyDescriptor(title = "Password", widget = "password")
  @FormParam("password")
  var password : String = uninitialized
  
  @PropertyDescriptor(title = "Device Name", widget = "text")
  var displayName : String = uninitialized
  
}

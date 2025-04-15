package com.anjunar.technologyspeaks.security

import com.anjunar.scala.mapper.annotations.Descriptor

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class WebAuthnLogin {

  @BeanProperty
  var $type : String = uninitialized

  @BeanProperty
  @Descriptor(title = "Email", widget = "email")
  var username: String = uninitialized
  
  @BeanProperty
  @Descriptor(title = "Device Name", widget = "text")
  var displayName : String = uninitialized
  
}

package com.anjunar.technologyspeaks.security

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class WebAuthnCredentialRegistration {

  @BeanProperty
  var username: String = uninitialized

  @BeanProperty
  var displayName: String = uninitialized

  @BeanProperty
  var response: String = uninitialized
}

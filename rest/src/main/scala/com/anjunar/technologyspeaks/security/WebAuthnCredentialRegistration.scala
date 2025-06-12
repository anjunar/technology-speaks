package com.anjunar.technologyspeaks.security

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class WebAuthnCredentialRegistration {

  var username: String = uninitialized

  var displayName: String = uninitialized

  var response: String = uninitialized
}

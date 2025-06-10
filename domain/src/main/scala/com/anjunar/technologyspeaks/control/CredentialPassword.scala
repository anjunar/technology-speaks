package com.anjunar.technologyspeaks.control

import jakarta.persistence.Entity

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
class CredentialPassword extends Credential {
  
  @BeanProperty
  var password : String = uninitialized

}

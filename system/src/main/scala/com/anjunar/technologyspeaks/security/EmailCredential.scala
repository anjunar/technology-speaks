package com.anjunar.technologyspeaks.security

import jakarta.security.enterprise.credential.{AbstractClearableCredential, Password}


class EmailCredential(private val email: String, private val password: Password) extends AbstractClearableCredential {
  def getEmail: String = email

  def getPassword: Password = password

  override protected def clearCredential(): Unit = {
    password.clear()
  }

  def getPasswordAsString: String = String.valueOf(getPassword.getValue)
}

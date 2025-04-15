package com.anjunar.technologyspeaks.security

import jakarta.security.enterprise.credential.{AbstractClearableCredential, Password}

import java.time.LocalDate


class CivilCredential(private val firstName: String, private val lastName: String, private val birthdate: LocalDate, private val password: Password) extends AbstractClearableCredential {
  def getFirstName: String = firstName

  def getLastName: String = lastName

  def getBirthdate: LocalDate = birthdate

  def getPassword: Password = password

  def getPasswordAsString: String = String.valueOf(getPassword.getValue)

  override def clearCredential(): Unit = {
    password.clear()
  }
}

package com.anjunar.technologyspeaks.security

trait IdentityContext {
  def getPrincipal: SecurityCredential
}

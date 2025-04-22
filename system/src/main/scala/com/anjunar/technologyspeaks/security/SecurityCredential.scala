package com.anjunar.technologyspeaks.security

trait SecurityCredential {
  
  def hasRole(name : String) : Boolean

  def user : SecurityUser
  
}

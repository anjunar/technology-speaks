package com.anjunar.technologyspeaks.jaxrs.types

import com.anjunar.technologyspeaks.security.SecurityUser

trait OwnerProvider {
  def owner: SecurityUser
}

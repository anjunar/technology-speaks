package com.anjunar.technologyspeaks

import com.anjunar.technologyspeaks.control.*
import com.anjunar.technologyspeaks.jpa.Pair
import jakarta.enterprise.context.{ApplicationScoped, Initialized}
import jakarta.enterprise.event.Observes
import jakarta.servlet.ServletContext
import jakarta.transaction.Transactional

import java.time.LocalDate
import java.util.Objects


@ApplicationScoped
class DataImport {

  @Transactional
  def init(@Observes @Initialized(classOf[ApplicationScoped]) init: ServletContext): Unit = {
    var administrator = Role.query(Pair("name", "Administrator"))
    if (Objects.isNull(administrator)) {
      administrator = new Role
      administrator.name = "Administrator"
      administrator.description = "Administrator"
      administrator.persist()
    }
    var user = Role.query(Pair("name", "User"))
    if (Objects.isNull(user)) {
      user = new Role
      user.name = "User"
      user.description = "Benutzer"
      user.persist()
    }
    var guest = Role.query(Pair("name", "Guest"))
    if (Objects.isNull(guest)) {
      guest = new Role
      guest.name = "Guest"
      guest.description = "Gast"
      guest.persist()
    }

    var patrick = User.findByEmail("anjunar@gmx.de")

    if (Objects.isNull(patrick)) {
      val info = new UserInfo
      info.firstName = "Patrick"
      info.lastName = "Bittner"
      info.birthDate = LocalDate.of(1980, 4, 1)

      val address = new Address
      address.street = "Beim alten Schützenhof"
      address.number = "28"
      address.zipCode = "22083"
      address.country = "Deutschland"

      val token = new Credential
      token.roles.add(administrator)

      val email = new EMail()
      token.email = email

      email.value = "anjunar@gmx.de"
      email.credentials.add(token)

      patrick = new User
      email.user = patrick

      patrick.nickName = "Anjunar"
      patrick.enabled = true
      patrick.deleted = false
      patrick.info = info
      patrick.address = address
      patrick.emails.add(email)

      patrick.persist()
    }
  }
}

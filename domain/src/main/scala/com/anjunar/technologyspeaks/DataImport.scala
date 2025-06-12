package com.anjunar.technologyspeaks

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.scala.universe.ClassPathResolver
import com.anjunar.technologyspeaks.control.*
import com.anjunar.technologyspeaks.jaxrs.link.LinkDescription
import com.anjunar.technologyspeaks.jpa.Pair
import com.anjunar.technologyspeaks.olama.{ChatMessage, ChatRequest, OLlamaService}
import com.anjunar.technologyspeaks.shared.i18n.{I18n, Translation}
import com.typesafe.scalalogging.Logger
import jakarta.annotation.Resource
import jakarta.enterprise.context.{ApplicationScoped, Initialized}
import jakarta.enterprise.event.Observes
import jakarta.persistence.EntityManager
import jakarta.servlet.ServletContext
import jakarta.transaction.{Transactional, UserTransaction}

import java.time.LocalDate
import java.util.{Locale, Objects}
import scala.compiletime.uninitialized


@ApplicationScoped
class DataImport {

  val log: Logger = Logger[DataImport]

  @Resource
  var transaction: UserTransaction = uninitialized

  def init(@Observes @Initialized(classOf[ApplicationScoped]) init: ServletContext, oLlamaService: OLlamaService, entityManager : EntityManager): Unit = {
    transaction.begin()
    var administrator = Role.query(("name", "Administrator"))
    if (Objects.isNull(administrator)) {
      administrator = new Role
      administrator.name = "Administrator"
      administrator.description = "Administrator"
      administrator.saveOrUpdate()
    }
    var user = Role.query(("name", "User"))
    if (Objects.isNull(user)) {
      user = new Role
      user.name = "User"
      user.description = "Benutzer"
      user.saveOrUpdate()
    }
    var guest = Role.query(("name", "Guest"))
    if (Objects.isNull(guest)) {
      guest = new Role
      guest.name = "Guest"
      guest.description = "Gast"
      guest.saveOrUpdate()
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

      val passwordCredential = new CredentialPassword
      passwordCredential.password = "patrick"
      passwordCredential.roles.add(administrator)
      email.credentials.add(passwordCredential)

      patrick.saveOrUpdate()
    }

    transaction.commit()

    ClassPathResolver.findAnnotation(classOf[PropertyDescriptor])
      .foreach(resolved => {

        transaction.begin()

        var i18n = I18n.find(resolved.annotation.title())

        if (i18n == null) {
          i18n = new I18n
          i18n.text = resolved.annotation.title()
          i18n.saveOrUpdate()
        }

        I18n.languages.filter(locale => i18n.translations.stream().noneMatch(translation => translation.locale == locale))
          .foreach(locale => {

            translateWithAI(oLlamaService, i18n, locale)

          })

        entityManager.flush()
        transaction.commit()

      })

    ClassPathResolver.findAnnotation(classOf[LinkDescription])
      .foreach(resolved => {

        transaction.begin()

        var i18n = I18n.find(resolved.annotation.value())

        if (i18n == null) {
          i18n = new I18n
          i18n.text = resolved.annotation.value()
          i18n.saveOrUpdate()
        }

        I18n.languages.filter(locale => i18n.translations.stream().noneMatch(translation => translation.locale == locale))
          .foreach(locale => {

            translateWithAI(oLlamaService, i18n, locale)

          })

        entityManager.flush()
        transaction.commit()

      })


  }

  private def translateWithAI(oLlamaService: OLlamaService, i18n: I18n, locale: Locale) = {
    log.info(s"Generating translation for :${i18n.text} to ${locale.getLanguage}")

    val prompt =
      s"""Translate the following English text into ${locale.getLanguage}.
         |
         |Important:
         |- Preserve all placeholders and formatting symbols exactly as they are.
         |- These include: `{variable}`, `%d`, `%s`, `\n`, `\t`, `:`, `"..."`, etc.
         |- Do not translate anything inside curly braces `{}`, percent signs `%`, or other code-like tokens.
         |- Keep the overall tone and meaning accurate and natural.
         |
         |Text:
         |${i18n.text}""".stripMargin

    val response = oLlamaService.chat(ChatRequest(Seq(ChatMessage(prompt))))

    val translation = new Translation
    translation.locale = locale
    translation.text = response.message.content.trim

    i18n.translations.add(translation)
  }
}

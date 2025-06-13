package com.anjunar.technologyspeaks.shared.i18n

import com.anjunar.scala.i18n
import com.anjunar.scala.i18n.I18nResolver
import com.anjunar.technologyspeaks.shared.i18n.I18n
import com.typesafe.scalalogging.Logger
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest

import java.util
import java.util.Locale
import java.util.stream.Collectors
import scala.compiletime.uninitialized

@ApplicationScoped
class CDII18nResolver extends I18nResolver {

  val log: Logger = Logger[CDII18nResolver]

  @Inject
  var request : HttpServletRequest = uninitialized

  private val translations: util.Map[String, i18n.I18n] = I18n.findAll().stream()
    .map(english => i18n.I18n(english.text, english.translations
      .stream()
      .map(translation => i18n.Translation(translation.text, translation.locale))
      .collect(Collectors.toMap((translation : i18n.Translation) => translation.language, (translation : i18n.Translation) => translation.text))
    ))
    .collect(Collectors.toMap((english : i18n.I18n) => english.text, (english : i18n.I18n) => english))

  def find(value : String) : String = {
    val i18n = translations.get(value)
    if (i18n == null) {
      log.warn("No translation found for: " + value)
      return value
    }
    var serverLanguage = request.getLocale.getLanguage
    if (serverLanguage.isBlank) {
      serverLanguage = request.getHeader("x-language")
    }
    i18n.translations.get(Locale.forLanguageTag(serverLanguage))
  }

}

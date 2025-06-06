package com.anjunar.technologyspeaks.shared.i18n

import com.anjunar.scala.mapper.annotations.Descriptor

import java.util.Locale
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Translation {

  @Descriptor(title = "Text", writeable = true, naming = true)
  @BeanProperty
  var text : String = uninitialized

  @Descriptor(title = "Language", writeable = true, naming = true)
  @BeanProperty
  var locale : Locale = uninitialized
  
  override def toString = s"Translation($text, $locale)"
}

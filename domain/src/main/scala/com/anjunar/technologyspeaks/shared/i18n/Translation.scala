package com.anjunar.technologyspeaks.shared.i18n

import java.util.Locale
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class Translation {

  @BeanProperty
  var text : String = uninitialized
  
  @BeanProperty
  var locale : Locale = uninitialized

}

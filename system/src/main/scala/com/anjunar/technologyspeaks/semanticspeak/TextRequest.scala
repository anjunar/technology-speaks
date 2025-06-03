package com.anjunar.technologyspeaks.semanticspeak

import java.util
import scala.beans.BeanProperty

class TextRequest {

  @BeanProperty
  val texts: util.List[String] = new util.ArrayList[String]()

}

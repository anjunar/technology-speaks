package com.anjunar.technologyspeaks.configuration.jaxrs.exception

import java.util
import scala.beans.BeanProperty

case class Violation(@BeanProperty message : String, @BeanProperty clazz : String, @BeanProperty path : util.List[AnyRef])

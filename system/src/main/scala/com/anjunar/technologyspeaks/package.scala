package com.anjunar.technologyspeaks

import java.util
import scala.language.implicitConversions
import scala.jdk.CollectionConverters.*
import scala.collection.mutable

package object core {

  implicit def autoConvertList[E](j: util.List[E]): mutable.Buffer[E] = j.asScala

  implicit def autoConvertSet[E](j: util.Set[E]) : mutable.Set[E] = j.asScala

  implicit def autoConvertMap[K,V](j: util.Map[K,V]) : mutable.Map[K,V] = j.asScala

}

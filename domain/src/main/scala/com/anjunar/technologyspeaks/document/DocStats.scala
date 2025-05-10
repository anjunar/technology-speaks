package com.anjunar.technologyspeaks.document

import scala.beans.BeanProperty

case class DocStats(@BeanProperty var document: Document,
                    @BeanProperty var count: Long,
                    @BeanProperty var totalDistance: Double) {
  def avgDistance: Double = totalDistance / count
}
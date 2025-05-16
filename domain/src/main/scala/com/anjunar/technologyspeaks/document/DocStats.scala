package com.anjunar.technologyspeaks.document

import scala.beans.BeanProperty

case class DocStats(document: Document,
                    count: Long,
                    totalDistance: Double) {
  def avgDistance: Double = totalDistance / count
}
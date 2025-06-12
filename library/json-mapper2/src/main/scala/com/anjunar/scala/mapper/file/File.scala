package com.anjunar.scala.mapper.file

import scala.beans.BeanProperty

trait File {

  @BeanProperty
  def contentType: String
  def contentType_=(value: String): Unit

  @BeanProperty
  def name: String
  def name_=(value: String): Unit

  @BeanProperty
  def data: Array[Byte]
  def data_=(value: Array[Byte]): Unit

}

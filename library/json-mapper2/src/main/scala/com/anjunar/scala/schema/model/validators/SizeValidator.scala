package com.anjunar.scala.schema.model.validators

import com.anjunar.scala.mapper.annotations.IgnoreFilter

import scala.beans.BeanProperty

@IgnoreFilter
case class SizeValidator(@BeanProperty min : Int,@BeanProperty max : Int) extends Validator 

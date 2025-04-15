package com.anjunar.scala.mapper

import com.anjunar.scala.mapper.converters.*
import com.anjunar.scala.universe.ResolvedClass

class ConverterRegistry {
  
  val converters: Array[AbstractConverter] = Array(
    new ArrayConverter,
    new BooleanConverter,
    new ByteConverter,
    new EnumConverter,
    new LocaleConverter,
    new MapConverter,
    new NumberConverter,
    new StringConverter,
    new TemporalAmountConverter,
    new TemporalConverter,
    new UUIDConverter,
    new BeanConverter
  )

  def find(aClass : ResolvedClass): AbstractConverter = converters.find(converter => {
    aClass <:< converter.aClass
  }).orNull

}

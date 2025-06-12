package com.anjunar.scala.introspector

import com.anjunar.scala.universe.introspector.AbstractProperty
import com.anjunar.scala.universe.members.{ResolvedField, ResolvedMethod}

class DescriptorProperty(val owner: DescriptorsModel,
                         name: String,
                         field: ResolvedField,
                         getter: ResolvedMethod,
                         setter: ResolvedMethod) extends AbstractProperty(name, field, getter, setter) {

}

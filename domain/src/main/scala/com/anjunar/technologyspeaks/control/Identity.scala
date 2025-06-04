package com.anjunar.technologyspeaks.control


import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.scala.mapper.annotations.Descriptor
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

import scala.beans.BeanProperty


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
class Identity extends AbstractEntity {

  @BeanProperty
  @Descriptor(title = "Active")
  var enabled: Boolean = false

  @BeanProperty
  @Descriptor(title = "Deleted")
  var deleted : Boolean = false

}

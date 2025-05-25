package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.scala.mapper.annotations.Descriptor
import jakarta.persistence.{CascadeType, Embedded, Entity, OneToOne}
import jakarta.validation.constraints.{NotBlank, Pattern, Size}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
class Address extends AbstractEntity {

  @OneToOne(mappedBy = "address")
  @BeanProperty
  var user : User = uninitialized

  @Size(min = 3, max = 80)
  @BeanProperty
  @Descriptor(title = "Straße", naming = true)
  var street : String = uninitialized

  @Size(min = 0, max = 10)
  @BeanProperty
  @Descriptor(title = "Hausnummer", naming = true)
  var number : String = uninitialized

  @Pattern(regexp = "^\\d{5,5}$")
  @BeanProperty
  @Descriptor(title = "Postleitzahl")
  var zipCode : String = uninitialized

  @Size(min = 3, max = 80)
  @BeanProperty
  @Descriptor(title = "Land")
  var country : String = uninitialized
  
  @Embedded
  @BeanProperty
  @Descriptor(title = "Lat und Lan")
  var point : GeoPoint = uninitialized

}

object Address extends RepositoryContext[Address](classOf[Address])
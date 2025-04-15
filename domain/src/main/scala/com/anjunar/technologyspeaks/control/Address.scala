package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.scala.mapper.annotations.Descriptor
import jakarta.persistence.{Embedded, Entity}
import jakarta.validation.constraints.{NotBlank, Pattern, Size}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
class Address extends AbstractEntity {

  @NotBlank
  @Size(min = 3, max = 80)
  @BeanProperty
  @Descriptor(title = "Straße", naming = true)
  var street : String = uninitialized

  @NotBlank
  @Size(min = 0, max = 10)
  @BeanProperty
  @Descriptor(title = "Hausnummer", naming = true)
  var number : String = uninitialized

  @NotBlank
  @Pattern(regexp = "^\\d{5,5}$")
  @BeanProperty
  @Descriptor(title = "Postleitzahl")
  var zipCode : String = uninitialized

  @NotBlank
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
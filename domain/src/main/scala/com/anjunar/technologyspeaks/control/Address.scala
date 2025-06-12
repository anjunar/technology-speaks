package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.jpa.RepositoryContext
import com.anjunar.technologyspeaks.shared.AbstractEntity
import jakarta.persistence.{CascadeType, Embedded, Entity, OneToOne}
import jakarta.validation.constraints.{NotBlank, Pattern, Size}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
class Address extends AbstractEntity {

  @OneToOne(mappedBy = "address")
  var user : User = uninitialized

  @Size(min = 3, max = 80)
  @PropertyDescriptor(title = "Street", naming = true)
  var street : String = uninitialized

  @Size(min = 0, max = 10)
  @PropertyDescriptor(title = "Housenumber", naming = true)
  var number : String = uninitialized

  @Pattern(regexp = "^\\d{5,5}$")
  @PropertyDescriptor(title = "Zipcode")
  var zipCode : String = uninitialized

  @Size(min = 3, max = 80)
  @PropertyDescriptor(title = "Country")
  var country : String = uninitialized
  
  @Embedded
  @PropertyDescriptor(title = "Lat and Lan")
  var point : GeoPoint = uninitialized


  override def toString = s"Address($street, $number, $zipCode, $country)"
}

object Address extends RepositoryContext[Address](classOf[Address])
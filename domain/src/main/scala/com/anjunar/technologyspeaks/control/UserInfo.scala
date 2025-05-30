package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.media.Media
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jpa.{PostgresIndex, PostgresIndices}
import jakarta.persistence.*
import jakarta.validation.constraints.{NotBlank, NotNull, Past, Size}

import java.time.LocalDate
import scala.beans.BeanProperty
import scala.compiletime.uninitialized


@Entity
@PostgresIndices(Array(
  new PostgresIndex(name = "user_idx_firstName", columnList = "firstName", using = "GIN"),
  new PostgresIndex(name = "user_idx_lastName", columnList = "lastName", using = "GIN")
))
class UserInfo extends AbstractEntity {

  @OneToOne(mappedBy = "info")
  @BeanProperty
  var user : User = uninitialized

  @Size(min = 3, max = 80)
  @BeanProperty
  @Descriptor(title = "Vorname", naming = true)
  var firstName : String = uninitialized
  
  @Size(min = 3, max = 80)
  @BeanProperty
  @Descriptor(title = "Nachname", naming = true)
  var lastName : String = uninitialized
  
  @ManyToOne(cascade = Array(CascadeType.ALL))
  @BeanProperty
  @Descriptor(title = "Bild", widget = "image")
  var image : Media = uninitialized
  
  @Past
  @BeanProperty
  @Descriptor(title = "Geburtsdatum")
  var birthDate: LocalDate = uninitialized
  
}

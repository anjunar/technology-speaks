package com.anjunar.technologyspeaks.media

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import jakarta.persistence.{CascadeType, Entity, OneToOne, Table}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
class Media extends Thumbnail {

  @OneToOne(cascade = Array(CascadeType.ALL))
  @PropertyDescriptor(title = "Thumbnail")
  var thumbnail: Thumbnail = uninitialized

}



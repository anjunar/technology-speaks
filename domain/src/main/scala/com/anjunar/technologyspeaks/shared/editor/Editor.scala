package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.scala.mapper.annotations.Converter
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import jakarta.persistence.{CascadeType, Column, Convert, Entity, OneToMany, OneToOne}
import org.hibernate.annotations.Type

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
class Editor extends AbstractEntity {

  @OneToMany(cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @BeanProperty
  val files : util.List[File] = new util.ArrayList[File]()

  @Column(columnDefinition = "jsonb")
  @Type(classOf[RootType])
  @Converter(classOf[RootConverter])
  @BeanProperty
  var ast : Root = uninitialized

}

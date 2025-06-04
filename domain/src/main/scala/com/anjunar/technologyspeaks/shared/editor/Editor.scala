package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.scala.mapper.annotations.{Converter, Descriptor}
import com.anjunar.technologyspeaks.shared.AbstractEntity
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import jakarta.persistence.{CascadeType, Column, Convert, Entity, OneToMany, OneToOne, Transient}
import org.hibernate.annotations.Type
import org.hibernate.envers.Audited

import java.util
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
@Audited
class Editor extends AbstractEntity {

  @OneToMany(cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @BeanProperty
  val files : util.List[File] = new util.ArrayList[File]()

  @Column(columnDefinition = "jsonb")
  @Type(classOf[RootType])
  @Converter(classOf[RootConverter])
  @BeanProperty
  var json : Root = uninitialized

  @Transient
  @Descriptor(title = "Changes")
  @BeanProperty
  val changes: util.List[Change] = new util.ArrayList[Change]()

}

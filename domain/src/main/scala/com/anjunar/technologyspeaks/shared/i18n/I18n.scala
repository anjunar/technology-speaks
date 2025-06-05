package com.anjunar.technologyspeaks.shared.i18n

import com.anjunar.technologyspeaks.jpa.{Pair, RepositoryContext}
import com.anjunar.technologyspeaks.shared.{AbstractEntity}
import jakarta.persistence.{Column, Entity}
import org.hibernate.annotations.Type

import java.util
import java.util.Locale
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

@Entity
class I18n extends AbstractEntity {

  @BeanProperty
  var text: String = uninitialized

  @Column(columnDefinition = "jsonb")
  @Type(classOf[TranslationType])
  @BeanProperty
  val translations: util.Set[Translation] = new util.HashSet[Translation]()
  
} 

object I18n extends RepositoryContext[I18n](classOf[I18n]) {
  
  val languages: Array[Locale] = Array(Locale.FRENCH, Locale.GERMAN)
  
  def find(value : String) : I18n = {
    query(("text", value))
  }
  
}

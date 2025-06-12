package com.anjunar.technologyspeaks.shared.i18n

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.jaxrs.search.RestPredicate
import com.anjunar.technologyspeaks.jaxrs.search.provider.*
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import jakarta.ws.rs.QueryParam

import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class I18nSearch extends AbstractSearch {
  
  @PropertyDescriptor(title = "Text", writeable = true)
  @QueryParam("text")
  @RestPredicate(value = classOf[GenericSimilarityProvider[?]], property = "text")
  var text : String = uninitialized
  
}
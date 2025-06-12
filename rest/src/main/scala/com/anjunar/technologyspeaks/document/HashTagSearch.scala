package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.PropertyDescriptor
import com.anjunar.technologyspeaks.jaxrs.search.RestPredicate
import com.anjunar.technologyspeaks.jaxrs.search.provider.GenericNameProvider
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import org.jboss.resteasy.annotations.jaxrs.QueryParam

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class HashTagSearch extends AbstractSearch {
  
  @PropertyDescriptor(title = "Hash Tag", writeable = true)
  @QueryParam("value")
  @RestPredicate(classOf[GenericNameProvider[?]])
  var value : String = uninitialized

}

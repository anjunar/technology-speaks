package com.anjunar.technologyspeaks.document

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jaxrs.search.RestPredicate
import com.anjunar.technologyspeaks.jaxrs.search.provider.GenericNameProvider
import com.anjunar.technologyspeaks.jaxrs.types.AbstractSearch
import org.jboss.resteasy.annotations.jaxrs.QueryParam

import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class HashTagSearch extends AbstractSearch {
  
  @Descriptor(title = "Hash Tag", writeable = true)
  @QueryParam("value")
  @RestPredicate(classOf[GenericNameProvider[?]])
  @BeanProperty
  var value : String = uninitialized

}

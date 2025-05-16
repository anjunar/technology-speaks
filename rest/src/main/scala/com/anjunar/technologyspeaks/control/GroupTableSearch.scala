package com.anjunar.technologyspeaks.control

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jaxrs.search.{RestPredicate, RestSort}
import com.anjunar.technologyspeaks.jaxrs.search.provider.{GenericIdProvider, GenericNameProvider, GenericSortProvider}
import com.anjunar.technologyspeaks.jaxrs.types.{AbstractSearch, Sort}
import org.jboss.resteasy.annotations.jaxrs.QueryParam

import java.util
import java.util.UUID
import scala.beans.BeanProperty
import scala.compiletime.uninitialized

class GroupTableSearch extends AbstractSearch {

  @Descriptor(title = "Name", writeable = true)
  @RestPredicate(classOf[GenericNameProvider[?]])
  @BeanProperty
  private var name: String = uninitialized

}
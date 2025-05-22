package com.anjunar.technologyspeaks.jaxrs.types

import com.anjunar.scala.mapper.annotations.Descriptor
import com.anjunar.technologyspeaks.jaxrs.search.RestSort
import com.anjunar.technologyspeaks.jaxrs.search.provider.GenericSortProvider
import jakarta.ws.rs.QueryParam

import scala.beans.BeanProperty
import java.util

abstract class AbstractSearch {

  @Descriptor(title = "Sort", writeable = true, hidden = true)
  @RestSort(classOf[GenericSortProvider[?]])
  @QueryParam("sort")
  @BeanProperty
  val sort: util.List[String] = new util.ArrayList[String]()

  @Descriptor(title = "Index", writeable = true, hidden = true)
  @QueryParam("index")
  @BeanProperty
  var index = 0

  @Descriptor(title = "Limit", writeable = true, hidden = true)
  @QueryParam("limit")
  @BeanProperty
  var limit = 5

}

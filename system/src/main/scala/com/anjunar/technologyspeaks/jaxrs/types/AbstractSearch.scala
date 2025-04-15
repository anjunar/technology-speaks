package com.anjunar.technologyspeaks.jaxrs.types

import jakarta.ws.rs.QueryParam

import scala.beans.BeanProperty

abstract class AbstractSearch {

  @QueryParam("index")
  @BeanProperty
  var index = 0
  
  @QueryParam("limit")
  @BeanProperty
  var limit = 5

}

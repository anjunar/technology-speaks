package com.anjunar.technologyspeaks.configuration

import jakarta.ws.rs.ApplicationPath
import jakarta.ws.rs.core.Application

import java.util


/**
 * @author Patrick Bittner on 21.05.2015.
 */
@ApplicationPath("service")
class SimplicityApplication extends Application {

  override def getProperties: util.Map[String, AnyRef] = {
    val map = new util.HashMap[String, AnyRef]()
    map.put("resteasy.preferJacksonOverJsonB", "true")
    map
  }

}


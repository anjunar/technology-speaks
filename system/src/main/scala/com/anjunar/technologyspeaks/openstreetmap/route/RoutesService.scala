package com.anjunar.technologyspeaks.openstreetmap.route

import com.anjunar.technologyspeaks.configuration.jaxrs.ObjectMapperContextResolver
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import jakarta.ws.rs.client.{Client, ClientBuilder, WebTarget}
import jakarta.ws.rs.core.{HttpHeaders, MultivaluedHashMap}
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders

import scala.collection.mutable


object RoutesService {
  
  val apiKey = "pk.eyJ1IjoiYW5qdW5hciIsImEiOiJjbDFuczBnc20wd2g4M2NvMm1yMWp4aHpiIn0.1KbDOpN0gPaRq5MzS-N0Zw"

  case class Route(x1 : Double, y1 : Double, x2 : Double, y2 : Double) {
    override def toString : String = x1 + "," + y1 + ";" + x2 + "," + y2
  }

  val cache = new mutable.HashMap[Route, Response]()

  def find(route : Route): Response = {
    val option = cache.get(route)

    if (option.isDefined) {
      option.get
    } else {
      val client = ClientBuilder.newClient
      val target = client.target("https://api.mapbox.com/directions/v5")
      val webTarget = target.asInstanceOf[ResteasyWebTarget]
      val resteasyJacksonProvider = new ResteasyJackson2Provider() {}

      val mapper = ObjectMapperContextResolver.objectMapper

      resteasyJacksonProvider.setMapper(mapper)
      webTarget.register(resteasyJacksonProvider)
      val service: RouteResource = webTarget.proxy(classOf[RouteResource])

      val response = service.find("mapbox/driving-traffic", route.toString, "geojson", apiKey)

      cache.put(route, response)

      response
    }

  }


}

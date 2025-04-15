package com.anjunar.technologyspeaks.openstreetmap.geocoding

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import jakarta.ws.rs.client.ClientBuilder
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider

object GeoService {

  def find(street: String, number: String, zipCode: String, country: String): Response = {
    val client = ClientBuilder.newClient
    val target = client.target("https://nominatim.openstreetmap.org")
    val webTarget = target.asInstanceOf[ResteasyWebTarget]
    val resteasyJacksonProvider = new ResteasyJackson2Provider() {}

    val mapper = new ObjectMapper()
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .registerModule(new JavaTimeModule)

    resteasyJacksonProvider.setMapper(mapper)
    webTarget.register(resteasyJacksonProvider)
    val service: GeoResource = webTarget.proxy(classOf[GeoResource])
      service.find(number + " " + street + " " + ", " + zipCode + " " + country, "geojson")
  }


}

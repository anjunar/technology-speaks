package com.anjunar.technologyspeaks.openstreetmap.geocoding2

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import jakarta.ws.rs.client.{Client, ClientBuilder, WebTarget}
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider

object MapBoxService {
  def find(street: String, number: String, zipCode: String, country: String): FeatureCollection = {
    val client = ClientBuilder.newClient
    val target = client.target("https://api.mapbox.com/geocoding/v5")
    val webTarget = target.asInstanceOf[ResteasyWebTarget]
    val resteasyJacksonProvider = new ResteasyJackson2Provider() {}
    val mapper = new ObjectMapper()
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    
    resteasyJacksonProvider.setMapper(mapper)
    webTarget.register(resteasyJacksonProvider)
    val service = webTarget.proxy(classOf[MapBoxGeoCoding])
    val token = "pk.eyJ1IjoiYW5qdW5hciIsImEiOiJjbDFuczBnc20wd2g4M2NvMm1yMWp4aHpiIn0.1KbDOpN0gPaRq5MzS-N0Zw"
    service.execute(token, number + " " + street + " " + ", " + zipCode + " " + country, false, false, "de", "address")
  }
}

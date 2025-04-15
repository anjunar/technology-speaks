package com.anjunar.technologyspeaks.openstreetmap.geocoding2

import jakarta.ws.rs.*

@Path("mapbox.places") 
trait MapBoxGeoCoding {
  
  @GET
  @Path("{query}.json")
  @Produces(Array("application/vnd.geo+json;charset=utf-8")) 
  def execute(@QueryParam("access_token") token: String, 
              @PathParam("query") query: String, 
              @QueryParam("autocomplete") autocomplete: Boolean, 
              @QueryParam("fuzzyMatch") fuzzyMatch: Boolean, 
              @QueryParam("language") language: String, 
              @QueryParam("types") types: String): FeatureCollection
}

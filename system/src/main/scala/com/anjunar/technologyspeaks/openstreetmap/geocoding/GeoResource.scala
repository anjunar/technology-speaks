package com.anjunar.technologyspeaks.openstreetmap.geocoding

import jakarta.ws.rs.{GET, Path, QueryParam}

@Path("/")
trait GeoResource {

  @GET
  @Path("search")
  def find(@QueryParam("q") query: String, @QueryParam("format") format : String) : Response

}

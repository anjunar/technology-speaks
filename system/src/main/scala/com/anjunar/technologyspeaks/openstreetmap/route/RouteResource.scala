package com.anjunar.technologyspeaks.openstreetmap.route

import jakarta.ws.rs.core.{Context, HttpHeaders}
import jakarta.ws.rs.{GET, Path, PathParam, QueryParam}

@Path("/")
trait RouteResource {
  
  @GET
  @Path("/{profile}/{coordinates}")
  def find(@PathParam("profile") profile : String, 
           @PathParam("coordinates") coordinates : String,
           @QueryParam("geometries") geometries : String,
           @QueryParam("access_token") accessToken : String) : Response

}

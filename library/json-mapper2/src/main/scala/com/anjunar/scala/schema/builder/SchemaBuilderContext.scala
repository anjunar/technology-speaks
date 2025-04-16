package com.anjunar.scala.schema.builder

import jakarta.inject.Inject

import scala.compiletime.uninitialized

trait SchemaBuilderContext {

  @Inject
  var provider: SchemaBuilderProvider = uninitialized


}

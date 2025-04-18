package com.anjunar.technologyspeaks.shared

sealed trait SchemaView

object SchemaView {
  case object Compact extends SchemaView
  case object Full extends SchemaView
}

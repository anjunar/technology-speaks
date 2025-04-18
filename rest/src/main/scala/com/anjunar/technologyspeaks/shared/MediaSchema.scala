package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.media.Media

object MediaSchema {

  def static(builder: SchemaBuilder, isOwnedOrAdmin: Boolean) = {
    builder.forType(classOf[Media], (entity: EntitySchemaBuilder[Media]) => entity
        .property("id")
        .property("name", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("type", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("subType", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("data", property => property
          .withWriteable(isOwnedOrAdmin)
        )
        .property("thumbnail", property => property
          .withWriteable(isOwnedOrAdmin)
        )
      )
  }


}

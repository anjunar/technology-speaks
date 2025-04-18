package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.{EntitySchemaBuilder, SchemaBuilder}
import com.anjunar.technologyspeaks.media.Thumbnail

object ThumbnailSchema {

  def static(builder: SchemaBuilder, isOwnedOrAdmin: Boolean): Unit = {
    builder.forType(classOf[Thumbnail], (entity: EntitySchemaBuilder[Thumbnail]) => entity
      .property("id")
      .property("name", property => property
        .withWriteable(isOwnedOrAdmin)
      )
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
    )

  }

}

package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.EntitySchemaBuilder
import com.anjunar.technologyspeaks.control.GeoPoint
import com.anjunar.technologyspeaks.shared.editor.*
import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item

object EditorSchema {

  def static(builder: EntitySchemaBuilder[Editor]): EntitySchemaBuilder[Editor] = {
    builder
      .property("files", property => property
        .withWriteable(true)
        .forType(classOf[File], (builder : EntitySchemaBuilder[File]) => builder
          .property("name", property => property
            .withWriteable(true)
          )
          .property("type", property => property
            .withWriteable(true)
          )
          .property("subType", property => property
            .withWriteable(true)
          )
          .property("data", property => property
            .withWriteable(true)
          )
        )
      )
      .property("json", property => property
        .withWidget("editor")
        .withWriteable(true)
      )
  }

}

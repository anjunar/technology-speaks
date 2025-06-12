package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.EntitySchemaBuilder
import com.anjunar.technologyspeaks.shared.editor.*

object EditorSchema {

  def static(builder: EntitySchemaBuilder[Editor]): EntitySchemaBuilder[Editor] = {
    builder
      .property("id")
      .property("files", property => property
        .withWriteable(true)
        .forType(classOf[EditorFile], (builder: EntitySchemaBuilder[EditorFile]) => builder
          .property("id")
          .property("name", property => property
            .withWriteable(true)
          )
          .property("contentType", property => property
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
      .property("changes", property => property
        .forType(classOf[Change], (builder: EntitySchemaBuilder[Change]) => builder
          .property("action")
          .property("nodeType")
          .property("oldValue")
          .property("newValue")
          .property("value")
          .property("offset")
        )
      )
  }

}

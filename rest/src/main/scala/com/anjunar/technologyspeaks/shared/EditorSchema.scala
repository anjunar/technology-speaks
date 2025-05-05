package com.anjunar.technologyspeaks.shared

import com.anjunar.scala.schema.builder.EntitySchemaBuilder
import com.anjunar.technologyspeaks.control.GeoPoint
import com.anjunar.technologyspeaks.shared.editor.{AbstractNode, CodeNode, ImageNode, ItemNode, ListNode, ParagraphNode, RootNode, TableCellNode, TableNode, TableRowNode, TextNode}
import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item

object EditorSchema {

  def static(builder: EntitySchemaBuilder[RootNode]): EntitySchemaBuilder[RootNode] = {
    builder
      .property("domHeight", property => property
        .withWriteable(true)
      )
      .property("children", property => property
        .withWriteable(true)
        .forType(classOf[CodeNode], (builder : EntitySchemaBuilder[CodeNode]) => builder
          .property("domHeight", property => property
            .withWriteable(true)
          )
          .property("text", property => property
            .withWriteable(true)
          )
        )
        .forType(classOf[ImageNode], (builder : EntitySchemaBuilder[ImageNode]) => builder
          .property("domHeight", property => property
            .withWriteable(true)
          )
          .property("src", property => property
            .withWriteable(true)
          )
          .property("aspectRatio", property => property
            .withWriteable(true)
          )
          .property("height", property => property
            .withWriteable(true)
          )
          .property("width", property => property
            .withWriteable(true)
          )
        )
        .forType(classOf[ListNode], (builder : EntitySchemaBuilder[ListNode]) => builder
          .property("domHeight", property => property
            .withWriteable(true)
          )
          .property("children", property => property
            .withWriteable(true)
            .forType(classOf[ItemNode], (builder : EntitySchemaBuilder[ItemNode]) => builder
              .property("domHeight", property => property
                .withWriteable(true)
              )
              .property("children", property => property
                .withWriteable(true)
                .forType(classOf[AbstractNode], (builder : EntitySchemaBuilder[AbstractNode]) => builder
                  .property("domHeight", property => property
                    .withWriteable(true)
                  ))
              )
            )
          )
        )
        .forType(classOf[ParagraphNode], (builder : EntitySchemaBuilder[ParagraphNode]) => builder
          .property("domHeight", property => property
            .withWriteable(true)
          )
          .property("children", property => property
            .withWriteable(true)
            .forType(classOf[TextNode], (builder : EntitySchemaBuilder[TextNode]) => builder
              .property("domHeight", property => property
                .withWriteable(true)
              )
              .property("block", property => property
                .withWriteable(true)
              )
              .property("text", property => property
                .withWriteable(true)
              )
              .property("bold", property => property
                .withWriteable(true)
              )
              .property("italic", property => property
                .withWriteable(true)
              )
              .property("deleted", property => property
                .withWriteable(true)
              )
              .property("sub", property => property
                .withWriteable(true)
              )
              .property("sup", property => property
                .withWriteable(true)
              )
              .property("fontFamily", property => property
                .withWriteable(true)
              )
              .property("fontSize", property => property
                .withWriteable(true)
              )
              .property("color", property => property
                .withWriteable(true)
              )
              .property("backgroundColour", property => property
                .withWriteable(true)
              )
            )
          )
        )
        .forType(classOf[TableNode], (builder : EntitySchemaBuilder[TableNode]) => builder
          .property("domHeight", property => property
            .withWriteable(true)
          )
          .property("children", property => property
            .withWriteable(true)
            .forType(classOf[TableRowNode], (builder : EntitySchemaBuilder[TableRowNode]) => builder
              .property("domHeight", property => property
                .withWriteable(true)
              )
              .property("children", property => property
                .withWriteable(true)
                .forType(classOf[TableCellNode], (builder : EntitySchemaBuilder[TableCellNode]) => builder
                  .property("domHeight", property => property
                    .withWriteable(true)
                  )
                  .property("children", property => property
                    .withWriteable(true)
                    .forType(classOf[AbstractNode], (builder : EntitySchemaBuilder[AbstractNode]) => builder
                      .property("domHeight", property => property))
                  )
                )
              )
            )
          )
        )
      )
  }

}

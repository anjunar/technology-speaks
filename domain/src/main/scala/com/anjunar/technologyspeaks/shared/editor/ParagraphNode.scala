package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.scala.mapper.annotations.IgnoreFilter
import jakarta.persistence.Entity

@Entity
class ParagraphNode extends AbstractContainerNode[TextNode]

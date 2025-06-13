package com.anjunar.technologyspeaks.document

import com.anjunar.technologyspeaks.shared.editor.*
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.{TableBlock, TablesExtension}
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.ast
import com.vladsch.flexmark.util.ast.{Document as VDocument, Node as VNode}
import jakarta.enterprise.context.ApplicationScoped

import java.util
import java.util.{Spliterator, Spliterators}
import java.util.stream.StreamSupport
import scala.collection.immutable
import scala.jdk.CollectionConverters.*

@ApplicationScoped
class DocumentService {

  def saveOrUpdate(document : Document) : Unit = {

    val options = new MutableDataSet()
    options.set(Parser.EXTENSIONS, util.Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

    val parser = Parser.builder(options).build
    val node = parser.parse(document.editor.markdown)

    document.editor.json = convertAST(node).asInstanceOf[Root]

    document.saveOrUpdate()

  }

  private def convertAST(node: VNode): Node = node match {
    case node: VDocument =>
      val position: Position = createPosition(node)
      Root(position, children(node))
    case node : ast.FencedCodeBlock =>
      val position: Position = createPosition(node)
      Code(position, node.getInfo.unescape(), node.getChars.unescape())
    case node : ast.Emphasis =>
      val position: Position = createPosition(node)
      Emphasis(position, children(node))
    case node : ast.Heading =>
      val position: Position = createPosition(node)
      Heading(node.getLevel, position, children(node))
    case node : ast.Image =>
      val position: Position = createPosition(node)
      Image(position, node.getUrlContent.unescape(), node.getText.unescape())
    case node : ast.ListBlock =>
      val position: Position = createPosition(node)
      List(node.isTight,position, children(node))
    case node : ast.ListItem =>
      val position: Position = createPosition(node)
      ListItem(position, children(node))
    case node : ast.Paragraph =>
      val position: Position = createPosition(node)
      Paragraph(position, children(node))
    case node : ast.StrongEmphasis =>
      val position: Position = createPosition(node)
      Strong(position, children(node))
    case node: TableBlock =>
      val position = createPosition(node)
      val (headNodes, bodyNodes) = splitTableChildren(node)
      val headerRows = headNodes.map(convertTableRow)
      val bodyRows = bodyNodes.map(convertTableRow)
      Table(null, position, (headerRows ++ bodyRows).asJava)
    case node : ast.Text =>
      val position = createPosition(node)
      Text(position, node.getChars.unescape())

  }

  private def children(node: VNode) = {
    val iterator = node.getChildren.iterator().asScala
    val children: LazyList[Node] = iterator.to(LazyList).map(convertAST)
    children.asJava
  }

  private def createPosition(doc: VNode) = {
    val start = Marker(
      doc.lineColumnAtStart().getSecond,
      doc.lineColumnAtStart().getFirst,
      doc.getStartOffset
    )

    val end = Marker(
      doc.getLineColumnAtEnd.getSecond,
      doc.getLineColumnAtEnd.getFirst,
      doc.getEndOffset
    )

    val position = Position(start, end)
    position
  }

  private def splitTableChildren(node: TableBlock): (immutable.List[VNode], immutable.List[VNode]) = {
    val children = node.getChildren.iterator().asScala.toList
    val head = children.takeWhile(_.isInstanceOf[com.vladsch.flexmark.ext.tables.TableHead])
    val body = children.drop(head.size)
    (head.flatMap(_.getChildren.iterator().asScala), body.flatMap(_.getChildren.iterator().asScala))
  }

  private def convertTableRow(node: VNode): TableRow = {
    val position = createPosition(node)
    val cells = node.getChildren.iterator().asScala.map(convertTableCell).toList
    TableRow(position, cells.asJava)
  }

  private def convertTableCell(node: VNode): TableCell = {
    val position = createPosition(node)
    val contentNodes = children(node).asScala.toList
    TableCell(position, contentNodes.asJava)
  }
}

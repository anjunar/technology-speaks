package com.anjunar.technologyspeaks.shared.editor

import com.anjunar.scala.mapper.annotations.Descriptor
import com.github.gumtreediff.actions.model.{Action, Delete, Insert, Move, Update}
import com.github.gumtreediff.tree.Tree
import difflib.Delta.TYPE
import difflib.DiffUtils
import org.apache.commons.text.diff.{CommandVisitor, StringsComparator}

import scala.beans.BeanProperty
import scala.compiletime.uninitialized
import java.util
import scala.jdk.CollectionConverters.*
import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer

case class Change(

  @Descriptor(title = "Action")
  @BeanProperty
  action : String,

  @Descriptor(title = "Type")
  @BeanProperty
  nodeType : String,

  @Descriptor(title = "Old Value")
  @BeanProperty
  oldValue : String = null,

  @Descriptor(title = "New Value")
  @BeanProperty
  newValue : String = null,

  @Descriptor(title = "Value")
  @BeanProperty
  value : String = null,

  @Descriptor(title = "Offset")
  @BeanProperty
  offset : Int

)

object Change {

  def createChanges(position : Int, oldText: String, newText: String): List[Change] = {
    val oldList = oldText.toCharArray.map(_.toString).toList.asJava
    val newList = newText.toCharArray.map(_.toString).toList.asJava

    val patch = DiffUtils.diff(oldList, newList)
    patch.getDeltas.asScala.map { delta =>
      val offset = delta.getOriginal.getPosition
      delta.getType match {
        case TYPE.INSERT =>
          Change("insert", "text", value = delta.getRevised.getLines.asScala.mkString(""), offset = position + offset)
        case TYPE.DELETE =>
          Change("delete", "text", value = delta.getOriginal.getLines.asScala.mkString(""), offset = position + offset)
        case TYPE.CHANGE =>
          Change("update", "text",
            oldValue = delta.getOriginal.getLines.asScala.mkString(""),
            newValue = delta.getRevised.getLines.asScala.mkString(""),
            offset = position + offset)
      }
    }.toList
  }

  def extractChanges(editScript: util.List[Action]): util.List[Change] = {
    editScript.asScala.toList.flatMap {
      case ins: Insert =>
        List(Change("insert", ins.getNode.getType.name, value = ins.getNode.getLabel, offset = ins.getNode.getParent.getPos))
      case del: Delete =>
        List(Change("delete", del.getNode.getType.name, value = del.getNode.getLabel, offset = del.getNode.getParent.getPos))
      case upd: Update =>
        createChanges(upd.getNode.getParent.getPos, upd.getNode.getLabel, upd.getValue)
      case mov: Move =>
        List(Change("move", mov.getNode.getType.name, value = mov.getNode.getLabel, offset = mov.getNode.getParent.getPos))
    }.asJava
  }

}

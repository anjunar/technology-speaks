package com.anjunar.technologyspeaks.scala

import java.util
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

object CollectionExtension:
  extension [A](list: util.List[A])
    private def asBuf: mutable.Buffer[A] = list.asScala
  
    // Accessor-Methoden
    def apply(idx: Int): A = asBuf.apply(idx)
    def length: Int = asBuf.length
    def nonEmpty: Boolean = asBuf.nonEmpty
    def head: A = asBuf.head
    def tail: mutable.Buffer[A] = asBuf.tail
    def last: A = asBuf.last
    def init: mutable.Buffer[A] = asBuf.init
    def slice(from: Int, until: Int): mutable.Buffer[A] = asBuf.slice(from, until)
    def toSeq: Seq[A] = asBuf.toSeq
    def toList: List[A] = asBuf.toList
  
    // Transformationen
    def map[B](f: A => B): mutable.Seq[B] = asBuf.map(f)
    def flatMap[B](f: A => IterableOnce[B]): mutable.Seq[B] = asBuf.flatMap(f)
    def filter(p: A => Boolean): mutable.Seq[A] = asBuf.filter(p)
    def filterNot(p: A => Boolean): mutable.Seq[A] = asBuf.filterNot(p)
    def partition(p: A => Boolean): (mutable.Seq[A], mutable.Seq[A]) = asBuf.partition(p)
    def collect[B](pf: PartialFunction[A, B]): mutable.Seq[B] = asBuf.collect(pf)
  
    // Folding / Reducing
    def foldLeft[B](z: B)(op: (B, A) => B): B = asBuf.foldLeft(z)(op)
    def foldRight[B](z: B)(op: (A, B) => B): B = asBuf.foldRight(z)(op)
    def reduceLeft(op: (A, A) => A): A = asBuf.reduceLeft(op)
    def reduceRight(op: (A, A) => A): A = asBuf.reduceRight(op)
  
    // Queries
    def exists(p: A => Boolean): Boolean = asBuf.exists(p)
    def forall(p: A => Boolean): Boolean = asBuf.forall(p)
    def find(p: A => Boolean): Option[A] = asBuf.find(p)
    def count(p: A => Boolean): Int = asBuf.count(p)
  
    // Access subsequences
    def take(n: Int): mutable.Seq[A] = asBuf.take(n)
    def drop(n: Int): mutable.Seq[A] = asBuf.drop(n)
    def takeWhile(p: A => Boolean): mutable.Seq[A] = asBuf.takeWhile(p)
    def dropWhile(p: A => Boolean): mutable.Seq[A] = asBuf.dropWhile(p)
  
    // Mutable operations
    def +=(elem: A): this.type = {
      asBuf += elem; this
    }
    def +=:(elem: A): this.type = {
      asBuf.prepend(elem); this
    }
    def ++=(elems: IterableOnce[A]): this.type = {
      asBuf ++= elems; this
    }
  
    // Iteration
    def foreach(f: A => Unit): Unit = asBuf.foreach(f)
  
    // Misc
    def reverse: mutable.Seq[A] = asBuf.reverse
    def distinct: mutable.Seq[A] = asBuf.distinct
    def sorted[B >: A](implicit ord: Ordering[B]): mutable.Seq[A] = asBuf.sorted(ord)
  
  
  extension [A](set: util.Set[A])
    private def asMutSet: mutable.Set[A] = set.asScala
  
    // Accessor / Queries
    def nonEmpty: Boolean = asMutSet.nonEmpty
  
    // Mutable operations
    def +=(elem: A): this.type = {
      asMutSet += elem; this
    }
    def -=(elem: A): this.type = {
      asMutSet -= elem; this
    }
  
    // Transformations (return immutable Seq or Sets)
    def map[B](f: A => B): mutable.Set[B] = asMutSet.map(f)
    def flatMap[B](f: A => IterableOnce[B]): mutable.Set[B] = asMutSet.flatMap(f)
    def filter(p: A => Boolean): mutable.Set[A] = asMutSet.filter(p)
    def filterNot(p: A => Boolean): mutable.Set[A] = asMutSet.filterNot(p)
    def partition(p: A => Boolean): (mutable.Set[A], mutable.Set[A]) = asMutSet.partition(p)
  
    // Iteration
    def foreach(f: A => Unit): Unit = asMutSet.foreach(f)
  
    // Queries on predicates
    def exists(p: A => Boolean): Boolean = asMutSet.exists(p)
    def forall(p: A => Boolean): Boolean = asMutSet.forall(p)
    def count(p: A => Boolean): Int = asMutSet.count(p)
  
    // Conversion
    def toSeq: Seq[A] = asMutSet.toSeq
    def toList: List[A] = asMutSet.toList
  
  extension [K, V](map: util.Map[K, V])
    private def asMutMap: mutable.Map[K, V] = map.asScala
  
    // Abfrage-Methoden
    def nonEmpty: Boolean = asMutMap.nonEmpty
  
    // Transformationen
    def mapValues[W](f: V => W): mutable.Map[K, W] = asMutMap.mapValues(f).to(mutable.Map)
    def mapEntries[K2, V2](f: ((K, V)) => (K2, V2)): mutable.Map[K2, V2] = asMutMap.map(f).to(mutable.Map)
  
    def map[W](f: ((K, V)) => (K, W)): mutable.Map[K, W] = asMutMap.view.map { case (k, v) => (k, f((k, v))._2) }.to(mutable.Map)
  
    def flatMap[K2, V2](f: ((K, V)) => IterableOnce[(K2, V2)]): mutable.Map[K2, V2] = asMutMap.flatMap(f).to(mutable.Map)
  
    def filter(p: ((K, V)) => Boolean): mutable.Map[K, V] = asMutMap.filter(p)
    def filterKeys(p: K => Boolean): mutable.Map[K, V] = asMutMap.filterKeys(p).to(mutable.Map)
    def filterValues(p: V => Boolean): mutable.Map[K, V] = asMutMap.filter { case (_, v) => p(v) }
  
    // Iteration
    def foreach(f: ((K, V)) => Unit): Unit = asMutMap.foreach(f)
    def iterator: Iterator[(K, V)] = asMutMap.iterator
  
    // Conversion
    def toSeq: Seq[(K, V)] = asMutMap.toSeq
    def toList: List[(K, V)] = asMutMap.toList
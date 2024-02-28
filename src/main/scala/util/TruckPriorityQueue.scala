package util

import Traits.TruckLogic.Truck
import Traits.TruckLogic.TruckStatus

import scala.collection.mutable.ListBuffer

class TruckPriorityQueue(implicit val ord: Ordering[Truck]) {
  private var list = ListBuffer.empty[Truck]

  def enqueue(element: Truck): Unit = {
    list += element
    list = list.sortWith(ord.lt)
  }

  def remove(element: Truck): Unit = {
    list -= element
  }

  def dequeue(): Option[Truck] = {
    val first = list.headOption
    first.foreach(list -= _)
    first
  }

  def removeAt(index: Int): Option[Truck] = {
    if (index >= 0 && index < list.size) {
      Some(list.remove(index))
    } else {
      None
    }
  }

  def toStatus: List[TruckStatus] = list.toList.map(_.status)

  def toSeq: Seq[Truck] = list.toSeq

  def find(p: Truck => Boolean): Option[Truck] = list.find(p)
  
}
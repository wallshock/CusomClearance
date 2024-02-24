package Locations

import Traits.TruckLogic.TruckState
import Traits.TruckLogic.Truck

import scala.collection.mutable.ArrayBuffer

class Queue(maxSize: Int) {
  private val queue: ArrayBuffer[Truck] = ArrayBuffer()
  def enqueue(truck: Truck): Either[String, Unit] = {
    if (queue.size >= maxSize) Left("Queue is full")
    else {
      queue.append(truck)
      Right(())
    }
  }

  def dequeue(): Option[Truck] = {
    if (queue.isEmpty) None
    else Some(queue.remove(0))
  }

  def peek: Option[Truck] = {
    queue.headOption
  }

  def size: Int = {
    queue.size
  }

  def get(index: Int): Option[Truck] = {
    if (index >= 0 && index < queue.size) Some(queue(index))
    else None
  }
}
package Locations

import Traits.Location
import Traits.TruckLogic.{InQueue, Truck, TruckState}

import scala.collection.mutable.ArrayBuffer

class Queue(maxSize: Int, val queueIndex: Int) extends Location {
  private val elements: ArrayBuffer[Truck] = ArrayBuffer()
  private var gateWaitTime:Int = 0
  override def getLocation: String = "Queue"

  override def logEntry(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} entered the ${getLocation} at ${java.time.LocalDateTime.now}")
  }

  override def logExit(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} exited the ${getLocation} at ${java.time.LocalDateTime.now}")
  }
  def enqueue(truck: Truck): Either[String, Unit] = {
    logEntry(truck)
    if (elements.size >= maxSize) Left("Queue is full")
    else {
      elements.append(truck)
      Right(())
    }
  }

  def increaseGateWaitTime(time:Int): Unit = {
    gateWaitTime += time
  }
  def dequeue(): Option[Truck] = {
    if (elements.isEmpty) None
    else
      logExit(elements(0))
      Some(elements.remove(0))
  }

  //todo ugly
  def reduceGateCheckWaitTime(time:Int): Unit = {
    gateWaitTime -= time
    for(truck <- elements){
      truck.status.state match {
        case InQueue(queue, waitingTime) =>{
          truck.status.state = InQueue(queue,waitingTime-time)
        }
        case _ =>
      }
    }
  }

  def size: Int = {
    elements.size
  }

  def peek: Option[Truck] = {
    elements.headOption
  }

  def isFull: Boolean = {
    elements.size == maxSize
  }

  def get(index: Int): Option[Truck] = {
    if (index >= 0 && index < elements.size) Some(elements(index))
    else None
  }
  def waitingTime: Int = {
    var result = gateWaitTime
    for (element <- elements) {
      result += element.weight
    }
    result
  }

  def waitingTimeAt(index: Int): Int = {
    var result = gateWaitTime
    if (index >= elements.size) {
      result = waitingTime
    } else{
      for (i <- 0 until index) {
        if (i < elements.size) result += elements(i).weight else result += 0
      }
    }
    result
  }
}
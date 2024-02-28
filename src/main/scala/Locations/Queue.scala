package Locations

import Traits.Location
import Traits.TruckLogic.{InQueue, Truck}

import scala.collection.mutable.ArrayBuffer

class Queue(maxSize: Int, val queueIndex: Int) extends Location {
  private val truckArray: ArrayBuffer[Truck] = ArrayBuffer()
  private var gateWaitTime:Int = 0
  override def getLocation: String = "Queue"

  override def logEntry(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} entered the $getLocation at ${java.time.LocalDateTime.now}")
  }

  override def logExit(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} exited the $getLocation at ${java.time.LocalDateTime.now}")
  }
  def enqueue(truck: Truck): Either[String, Unit] = {
    if (truckArray.size >= maxSize) Left("Queue is full")
    else {
      truck.inQueue(queueIndex,waitingTime)
      truckArray.append(truck)
      Right(())
    }
  }
  
  def printr():Unit = {
    print(s"Queue$queueIndex: ")
    for(truck <- truckArray){
      print(s"${truck.weight} ")
    }
    print(s"| Wait Time: $waitingTime  Queue + GateWaitTime")
    println("")
  }

  def increaseGateWaitTime(time:Int): Unit = {
    gateWaitTime += time
  }
  def dequeue(): Option[Truck] = {
    if (truckArray.isEmpty) None
    else
      Some(truckArray.remove(0))
  }

  def setElementAt(index: Int, truck: Truck): Unit = {
    if (index >= 0 && index < truckArray.length) {
      for(i <- index until truckArray.length){
        val truckTemp = truckArray(i)
        truckTemp.inQueue(queueIndex, waitingTimeAt(index) + truck.weight)
      }
      truck.inQueue(queueIndex, waitingTimeAt(index))
      truckArray(index) = truck
    }
  }

  def reduceGateCheckWaitTime(time:Int): Unit = {
    gateWaitTime -= time

    for(truck <- truckArray){
      truck.status.state match {
        case InQueue(idx,timeOld) => truck.inQueue(idx,timeOld-time)
      }
    }
  }

  def removeAt(i: Int): Option[Truck] = {
    if (i >= 0 && i < truckArray.length) {
      for(index <- i until truckArray.length){
        val truck = truckArray(index)
        truck.status.state match {
          case InQueue(idx,waitTime) => truck.inQueue(idx,waitTime-truckArray(i).weight)
          case _ =>
        }
      }
      Some(truckArray.remove(i))
    } else None
  }

  def size: Int = {
    truckArray.size
  }

  def peek: Option[Truck] = {
    truckArray.headOption
  }

  def isFull: Boolean = {
    truckArray.size == maxSize
  }

  def get(index: Int): Truck = {
    if (index >= 0 && index < truckArray.size) truckArray(index)
    else throw new IndexOutOfBoundsException("Invalid index: " + index)
  }

  def waitingTime: Int = {
    var result = gateWaitTime
    for (element <- truckArray) {
      result += element.weight
    }
    result
  }

  def waitingTimeAt(index: Int): Int = {
    var result = gateWaitTime
    if (index >= truckArray.size) {
      result = waitingTime
    } else{
      for (i <- 0 until index) {
        if (i < truckArray.size) result += truckArray(i).weight else result += 0
      }
    }
    result
  }
}
import Locations.Queue
import Traits.TruckLogic.Truck

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.collection.immutable.List

class QueueManager(queues: List[Queue]) {

  def optimizeQueues(): Unit = {
    val (shorterQueue, longerQueue) = getShorterAndLongerQueues
    val (lowerWait, higherWait) = getLowerAndHigherQueues

    if (queuesAreUnequalSize) {
      optimizeUnequalQueues(shorterQueue, longerQueue, lowerWait, higherWait)
    }
  }

  private def queuesAreUnequalSize: Boolean = queues(0).size != queues(1).size

  private def optimizeUnequalQueues(shorterQueue: Queue, longerQueue: Queue, lowerWait: Queue, higherWait: Queue): Unit = {
    val shorterSize = shorterQueue.size
    val longerSize = longerQueue.size

    if (shorterQueue == higherWait) {
      optimizeQueuesWithShorterHigherWait(shorterQueue, longerQueue, lowerWait, higherWait, shorterSize, longerSize)
    } else {
      optimizeQueuesWithLongerHigherWait(shorterQueue, longerQueue, shorterSize, longerSize)
    }
  }

  private def optimizeQueuesWithShorterHigherWait(shorterQueue: Queue, longerQueue: Queue, lowerWait: Queue, higherWait: Queue, shorterSize: Int, longerSize: Int): Unit = {
    swapBetweenLowAndHighHeuristic(lowerWait, higherWait)
    val elementsToChange = getElementsToChange(shorterQueue, longerQueue, shorterSize, longerSize)
    moveTrucksFromLongerToShorterHeuristic(shorterQueue, longerQueue, elementsToChange)
  }

  private def optimizeQueuesWithLongerHigherWait(shorterQueue: Queue, longerQueue: Queue, shorterSize: Int, longerSize: Int): Unit = {
    val elementsToChange = getElementsToChange(shorterQueue, longerQueue, shorterSize, longerSize)
    moveTrucksFromLongerToShorterHeuristic(shorterQueue, longerQueue, elementsToChange)
    val (newShorterQueue, newLongerQueue) = getShorterAndLongerQueues
    swapBetweenShortAndLongHeuristic(newShorterQueue, newLongerQueue)
  }

  // This function determines which queue has less waiting time based on gateWayTime and index 0 (those we cannot change)
  private def getLowerAndHigherQueues: (Queue, Queue) = {
    if (queues(0).waitingTimeAt(1) < queues(1).waitingTimeAt(1)) (queues(0), queues(1))
    else (queues(1), queues(0))
  }

  private def getShorterAndLongerQueues: (Queue, Queue) = {
    if (queues(0).size < queues(1).size) (queues(0), queues(1))
    else (queues(1), queues(0))
  }

  // This function determines which elements should be moved from higherQueue to lowerQueue
  private def getElementsToChange(shorterQueue: Queue, longerQueue: Queue, shorterSize: Int, longerSize: Int): ListBuffer[Int] = {
    var toChange:ListBuffer[Int] = ListBuffer()
    var tempShorterWait = shorterQueue.waitingTime
    var tempLongerWait = longerQueue.waitingTimeAt(shorterSize)

    //handle when 0
    for (i <- shorterSize until longerSize) {
      if (tempLongerWait > tempShorterWait) {
        toChange += i
        tempShorterWait += longerQueue.get(i).weight
      } else {
        tempLongerWait += longerQueue.get(i).weight
      }
    }
    toChange
  }

  private def swapBetweenShortAndLongHeuristic(shorterQueue: Queue, longerQueue: Queue): Unit = {
    for (i <- 1 until shorterQueue.size) {
      if (shorterQueue.get(i).weight < longerQueue.get(i).weight) {
        swap(shorterQueue, longerQueue, i)
      }
    }
  }
  private def swapBetweenLowAndHighHeuristic(lowerWait: Queue, higherWait: Queue): Unit = {
    for (i <- 1 until higherWait.size) {
      if (lowerWait.get(i).weight > higherWait.get(i).weight) {
        swap(lowerWait, higherWait, i)
      }
    }
  }

  private def moveTrucksFromLongerToShorterHeuristic(lowerQueue: Queue, higherQueue: Queue, indicies: ListBuffer[Int]): Unit = {
    //going from bigger index to smaller to not destroy queue structure
    var tempList:ListBuffer[Truck] = ListBuffer()

    for (index <- indicies.reverse){
      tempList += higherQueue.get(index)
      higherQueue.removeAt(index)
    }

    for (truck <- tempList.reverse){
      lowerQueue.enqueue(truck)
      truck.inQueue(lowerQueue.queueIndex, lowerQueue.waitingTime)
    }
  }

  private def swap(lowerQueue: Queue, higherQueue: Queue, index: Int): Unit = {
    if (index >= 0 && index < lowerQueue.size && index < higherQueue.size) {
      val tempTruck = lowerQueue.get(index)
      val truck = higherQueue.get(index)
      truck.inQueue(lowerQueue.queueIndex, lowerQueue.waitingTimeAt(index))
      lowerQueue.setElementAt(index, truck)

      tempTruck.inQueue(higherQueue.queueIndex, lowerQueue.waitingTimeAt(index))
      higherQueue.setElementAt(index, tempTruck)
    }
  }

  def decreaseWaitingTimes(queueIndex:Int,time:Int): Unit = {
    val queue = queues(queueIndex)
    queue.reduceGateCheckWaitTime(time)
  }

  def add(truck:Truck): (Int,Int) ={
    val queue = getBestQueue
    queue.enqueue(truck)
    (queue.queueIndex,queue.waitingTime)
  }
  
  def areQueuesFull: Boolean = {
    queues.forall(_.isFull)
  }
  
  private def getBestQueue: Queue = {
    val availableQueues = queues.filterNot(_.isFull)
    availableQueues.minBy(_.waitingTime)
  }
}

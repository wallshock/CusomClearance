import Locations.Queue
import Traits.TruckLogic.Truck

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.collection.immutable.List

class QueueManager(queues: List[Queue]) {

  def optimizeQueues(): Unit = {
    if (queues(0).size != queues(1).size) {
      val (lowerWaitQueue, higherWaitQueue) = getLowerAndHigherQueues()

      val lowerSize = lowerWaitQueue.size
      val higherSize = higherWaitQueue.size

      // If lowerQueue is smaller than higherQueue, consider moving elements from higherQueue to lowerQueue
      if (lowerSize < higherSize) {
        val toChange = getElementsToChangeShorter(lowerWaitQueue, higherWaitQueue, lowerSize, higherSize)
        moveTrucksWhenQueueIsLowerAndShorter(lowerWaitQueue, higherWaitQueue, toChange)
        // If lowerQueue is now larger than higherQueue, consider swapping elements between the queues
      } else { //lowerQueue is also the longer queue so we have to calculate if moving elements is worth it
        val toChange = getElementsToChangeLonger(lowerWaitQueue, higherWaitQueue, lowerSize, higherSize)
        moveTrucksWhenQueueIsLowerAndLonger(lowerWaitQueue, higherWaitQueue, toChange)
      }
      if (lowerWaitQueue.size > higherWaitQueue.size) {
        swapElementsIfNecessary(lowerWaitQueue, higherWaitQueue)
      }
    }
  }

  // This function determines which queue has less waiting time based on gateWayTime and index 0 (those we cannot change)
  private def getLowerAndHigherQueues(): (Queue, Queue) = {
    if (queues(0).waitingTimeAt(1) < queues(1).waitingTimeAt(1)) (queues(0), queues(1))
    else (queues(1), queues(0))
  }

  // This function determines which elements should be moved from higherQueue to lowerQueue
  private def getElementsToChangeShorter(lowerQueue: Queue, higherQueue: Queue, lowerSize: Int, higherSize: Int): (ListBuffer[Int]) = {
    var toChange:ListBuffer[Int] = ListBuffer()
    var tempLowerWait = lowerQueue.waitingTime
    var tempHigherWait = if (lowerSize > 0) higherQueue.waitingTimeAt(lowerSize - 1) else higherQueue.waitingTimeAt(0)

    //handle when 0
    for (i <- lowerSize until higherSize) {
      if (tempHigherWait > tempLowerWait) {
        toChange += i
        tempLowerWait += higherQueue.get(i).weight
      }
    }
    toChange
  }
//todo
  private def getElementsToChangeLonger(lowerQueue: Queue, higherQueue: Queue, lowerSize: Int, higherSize: Int): (ListBuffer[Int]) = {
    var toChange: ListBuffer[Int] = ListBuffer()
    var tempWaitingTimeOfShorterQueue = lowerQueue.waitingTime
    var tempWaitingTimeOfLongerQueue = if (lowerSize > 0) higherQueue.waitingTimeAt(lowerSize - 1) else higherQueue.waitingTimeAt(0)

    //handle when 0
    for (i <- higherSize until lowerSize) {
      if (tempHigherWait > tempLowerWait) {
        toChange += i
        tempLowerWait += higherQueue.get(i).weight
      }
    }
    toChange
  }

  // This function swaps elements between lowerQueue and higherQueue if necessary
  private def swapElementsIfNecessary(lowerQueue: Queue, higherQueue: Queue): Unit = {
    for (i <- 1 until higherQueue.size) {
      if (lowerQueue.get(i).weight > higherQueue.get(i).weight) {
        swap(lowerQueue, higherQueue, i)
      }
    }
  }

  private def moveTrucksWhenQueueIsLowerAndShorter(lowerQueue: Queue, higherQueue: Queue, indicies: ListBuffer[Int]): Unit = {
    //going from bigger index to smaller to not destroy queue structure
    var tempList:ListBuffer[Truck] = ListBuffer()

    for (index <- indicies.reverse){
      tempList += higherQueue.get(index)
      higherQueue.removeAt(index)
    }

    for (truck <- tempList.reverse){
      lowerQueue.enqueue(truck)
    }
  }

  private def moveTrucksWhenQueueIsLowerAndLonger(lowerQueue: Queue, higherQueue: Queue, indicies: ListBuffer[Int]): Unit = {
    //going from bigger index to smaller to not destroy queue structure
    var tempList: ListBuffer[Truck] = ListBuffer()

    for (index <- indicies.reverse) {
      tempList += lowerQueue.get(index)
      higherQueue.removeAt(index)
    }

    for (truck <- tempList.reverse) {
      lowerQueue.enqueue(truck)
    }
  }

  private def swap(lowerQueue: Queue, higherQueue: Queue, index: Int): Unit = {
    if (index >= 0 && index < lowerQueue.size && index < higherQueue.size) {
      val temp = lowerQueue.get(index)
      lowerQueue.setElementAt(index, higherQueue.get(index))
      higherQueue.setElementAt(index, temp)
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

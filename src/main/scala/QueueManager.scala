import Locations.Queue
import Traits.TruckLogic.Truck

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.collection.immutable.List

class QueueManager(queues: List[Queue]) {

  def optimizeQueues(): Unit = {
    if (queues(0).size != queues(1).size) {
      val (shorterQueue, longerQueue) = getShorterAndLongerQueues()
      val (lowerWait,higherWait) = getLowerAndHigherQueues()
      val shorterSize = shorterQueue.size
      val longerSize = longerQueue.size

      // If lowerQueue is smaller than higherQueue, consider moving elements from higherQueue to lowerQueue
      if(shorterQueue==higherWait) {
        //short is bad
        swapBetweenLowAndHighHeuristic(lowerWait, higherWait)
        val elementsToChange = getElementsToChange(shorterQueue, longerQueue, shorterSize, longerSize)
        moveTrucksFromLongerToShorterHeuristic(shorterQueue, longerQueue, elementsToChange)

      }else{
        val elementsToChange = getElementsToChange(shorterQueue, longerQueue, shorterSize, longerSize)
        moveTrucksFromLongerToShorterHeuristic(shorterQueue, longerQueue, elementsToChange)
        val (newShorterQueue, newLongerQueue) = getShorterAndLongerQueues()
        swapBetweenShortAndLongHeuristic(newShorterQueue, newLongerQueue)
      }
    }
  }

  // This function determines which queue has less waiting time based on gateWayTime and index 0 (those we cannot change)
  private def getLowerAndHigherQueues(): (Queue, Queue) = {
    if (queues(0).waitingTimeAt(1) < queues(1).waitingTimeAt(1)) (queues(0), queues(1))
    else (queues(1), queues(0))
  }

  private def getShorterAndLongerQueues(): (Queue, Queue) = {
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

//todo take a look
  private def swapBetweenLowAndHighHeuristic(lowerWait: Queue, higherWait: Queue): Unit = {
    for (i <- 1 until higherWait.size) {
      if (higherWait.get(i).weight < lowerWait.get(i).weight) {
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

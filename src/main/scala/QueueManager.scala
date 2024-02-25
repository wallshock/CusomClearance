import Locations.Queue
import Traits.TruckLogic.Truck

import scala.collection.mutable.ListBuffer

class QueueManager(queues: ListBuffer[Queue]) {
  def optimizeQueues(): Unit = {
    val minSize = queues.minBy(_.size).size
    val maxSize = queues.maxBy(_.size).size
    if (minSize != maxSize) {
      val shortestQueues = queues.filter(_.size == minSize)
      val longestQueues = queues.filter(_.size == maxSize)

      if theres more shortest than longest take max values from longques
      else if theres more longest take max from shortest
    }

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

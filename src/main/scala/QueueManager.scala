import Locations.Queue
import Traits.TruckLogic.Truck

import scala.collection.mutable.ListBuffer

class QueueManager(queues: ListBuffer[Queue]) {
  def optimizeQueues(): Unit = {
    //todo
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

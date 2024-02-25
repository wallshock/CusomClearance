import Locations.Queue
import Traits.TruckLogic.Truck

import scala.collection.mutable.ListBuffer

class QueueManager(queues: ListBuffer[Queue]) {
  def optimizeQueues(): Unit = {
    //we calculate minimum and maximum size of queue
    val minSize = queues.minBy(_.size).size
    val maxSize = queues.maxBy(_.size).size
    
    //if we have different sizes we perform optimization
    if (minSize != maxSize) {
      val shortestQueues = queues.filter(_.size == minSize)
      val longestQueues = queues.filter(_.size == maxSize)
      val shorterSize = shortestQueues.length
      val longerSize = shortestQueues.length
      
      //if we dont have same ammount of long and short queues we need to pick
      //the best ones that will give us the most profit
      
      if(shorterSize != longerSize){
        if(shorterSize > longerSize) {
          for(i <- 1 until minSize) {
            //we take shortQueues with lowestElements
            //this is Queue class so give it apply(i)
            val queuesToSwap = shortestQueues.sortBy(_(i)).take(longerSize)
            
          }
        } else {
          for (i <- 1 until minSize) {
            //we take longQueues with highestElements
            //this is Queue class so give it apply(i)
            val queuesToSwap = longestQueues.sortBy(-_(i)).take(shorterSize)
          }
        }
      }else {
          
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

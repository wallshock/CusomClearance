import Locations.{DocumentControlGate, GoodsControlGate, OutOfSystem, Queue}
import Traits.ControlGate
import Traits.TruckLogic.*

import scala.collection.mutable
import scala.collection.immutable.List

class CustomClearanceSystemForTest(val queueSize:Int) {

  private val truckList: mutable.PriorityQueue[Truck] = mutable.PriorityQueue[Truck]()
  private val queues: List[Queue] = List(Queue(queueSize,0), Queue(queueSize,1))
  private val documentControlGate: DocumentControlGate = DocumentControlGate()
  private val goodsControlGates: List[GoodsControlGate] = List(GoodsControlGate(),GoodsControlGate())
  private val truckManager: TruckManager = TruckManager()
  private val queueManager: QueueManager = QueueManager(queues)

  private def handleState(truck: Truck): Unit = {
    truck.status.state match {
      case Arrived => handleArrival(truck)
      case DocumentCheck => handleDocumentCheck(truck)
      case Staging => handleJoiningQueue(truck)
      case InQueue(_,_) => handleQueueStep(truck)
      case GoodsCheck(_,_) => handleGoodsCheck(truck)
      case Departed => handleDeparture(truck)
    }
  }

  private def handleArrival(truck: Truck): Unit = {
    truck.status.state = DocumentCheck
    truckManager.sendTruckTo(truck,documentControlGate)
  }

  private def handleDocumentCheck(truck: Truck): Unit = {
    truck.status.state = if (documentControlGate.checkTruck(truck)) Staging else Departed
  }

  private def handleJoiningQueue(truck: Truck): Unit = {
    if(!queueManager.areQueuesFull) {
      
      val (queueIndex, waitingTime) = queueManager.add(truck)
      truck.status.state = InQueue(queueIndex, waitingTime)
      truckManager.sendTruckTo(truck,queues(queueIndex))
    }
  }


  private def handleQueueStep(truck: Truck): Unit = {
    val queueIndex = getTruckQueueIndex(truck)
    val queue = queues(queueIndex)
    val goodsControlGate = goodsControlGates(queueIndex)
    queue.peek match {
      case Some(peekTruck) if peekTruck == truck =>
        if (goodsControlGate.isGateFree) {
          queue.increaseGateWaitTime(truck.weight)
          truck.status.state = GoodsCheck(queueIndex)
          queue.dequeue()
          goodsControlGate.occupy(truck)
        }
      case _ =>
    }
  }
  
  def getWaitTime(idx:Int):Int={
    queues(idx).waitingTime
  }

  private def handleGoodsCheck(truck: Truck): Unit = {
    val gateIndex = getTruckGateIndex(truck)
    val goodsControlGate = goodsControlGates(gateIndex)
    val weightChecked = goodsControlGate.checkingProcess(truck)

    if (weightChecked != -1 && weightChecked < truck.weight) {
      handleWeightChecked(truck, gateIndex, weightChecked, goodsControlGate)
    } else {
      handleWeightNotChecked(truck, gateIndex, goodsControlGate)
    }
  }

  private def handleWeightChecked(truck: Truck, gateIndex: Int, weightChecked: Int, goodsControlGate: GoodsControlGate): Unit = {
    truck.status.state = GoodsCheck(gateIndex, weightChecked)
    queueManager.decreaseWaitingTimes(gateIndex, goodsControlGate.weightCheckTempo)
  }

  private def handleWeightNotChecked(truck: Truck, gateIndex: Int, goodsControlGate: GoodsControlGate): Unit = {
    truck.status.state match {
      case GoodsCheck(gateIdx, weight) => queueManager.decreaseWaitingTimes(gateIndex, truck.weight - weight)
      case _ =>
    }
    truck.status.state = Departed
    goodsControlGate.release(truck)
    truckManager.sendTruckTo(truck,OutOfSystem())
  }

  private def handleDeparture(truck: Truck): Unit = {
    truck.status.location = OutOfSystem()
    truckList.dequeue()
    //no need to change - garbage collection
  }

  private def getTruckQueueIndex(truck: Truck): Int = {
    var queueIndex = 0
    truck.status.state match {
      case InQueue(index, _) =>
        queueIndex = index
      case _ => Error("Not in queue")
    }
    queueIndex
  }

  private def getTruckGateIndex(truck: Truck): Int = {
    var gateIndex = 0
    truck.status.state match {
      case GoodsCheck(index, _) =>
        gateIndex = index
      case _ => Error("Not in goodsCheckGate")
    }
    gateIndex
  }

  def arrive(weight: Int): String = {
    val truck = CargoTruck(weight)
    truckList += truck
    truck.truckId
  }

  def queuesStatus(): Unit ={
    val minSize = queues.minBy(_.size).size
    val maxSize = queues.maxBy(_.size).size
  }

  def step(): Unit = {

    for (truck <- truckList) {
      handleState(truck)
    }

    queueManager.optimizeQueues()
  }
  def status: List[TruckStatus] = {
    truckList.map(truck => truck.status).toList
  }

  def findStatusById(truckId: String): TruckStatus = {
    truckList.find(_.truckId == truckId).map(_.status).getOrElse(throw new NoSuchElementException(s"No status found for truckId $truckId"))
  }


  private def waitingTime(truckId: String): Option[Int] = {
    val truckOpt = findTruckById(truckId)
    truckOpt match {
      case Some(truck) =>
        extractWaitingTime(truck)
      case None =>
        None // Truck not found
    }
  }

  private def findTruckById(truckId: String): Option[Truck] = {
    truckList.find(_.truckId == truckId)
  }

  private def extractWaitingTime(truck: Truck): Option[Int] = {
    truck.status.state match {
      case InQueue(_, waitingTime) =>
        Some(waitingTime)
      case _ =>
        None // Waiting time not yet established or truck status is not InQueue
    }
  }
}
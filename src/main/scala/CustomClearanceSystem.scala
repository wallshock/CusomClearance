import Locations.{DocumentControlGate, GoodsControlGate, InSystem, OutOfSystem, Queue, Waiting}
import Traits.TruckLogic.*
import util.TruckPriorityQueue

import scala.collection.mutable
import scala.collection.immutable.List

object CustomClearanceSystem {

  private val truckList: TruckPriorityQueue = new TruckPriorityQueue()
  private val queues: List[Queue] = List(Queue(5,0), Queue(5,1))
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
      case CustomCleared => handleDeparture(truck)
    }
  }

  private def handleArrival(truck: Truck): Unit = {
    truck.checkDocument()
    truckManager.sendTruckTo(truck,documentControlGate)
  }

  private def handleDocumentCheck(truck: Truck): Unit = {
    if (documentControlGate.checkTruck(truck))
      println("Document Check Successfull, moving to Staging")
      truck.moveToStaging()
    else
      println("Document check went wrong, Departing")
      truckManager.sendTruckTo(truck,OutOfSystem())
      truck.depart()
  }

  private def handleJoiningQueue(truck: Truck): Unit = {
    if(!queueManager.areQueuesFull) {
      val (queueIndex, _) = queueManager.add(truck)
      truckManager.sendTruckTo(truck, queues(queueIndex))
    }
  }


  private def handleQueueStep(truck: Truck): Unit = {
    getTruckQueueIndex(truck) match {
      case Some(queueIndex) => processQueueStep(truck, queueIndex)
      case None => throw new Exception("Truck is not in a queue")
    }
  }

  private def processQueueStep(truck: Truck, queueIndex: Int): Unit = {
    val queue = queues(queueIndex)
    val goodsControlGate = goodsControlGates(queueIndex)

    queue.peek match {
      case Some(peekTruck) if peekTruck == truck && goodsControlGate.isGateFree =>
        processTransitionToGoodControlGate(truck, queue, goodsControlGate, queueIndex)
      case _ =>
    }
  }

  private def processTransitionToGoodControlGate(truck: Truck, queue: Queue, goodsControlGate: GoodsControlGate, queueIndex: Int): Unit = {
    queue.increaseGateWaitTime(truck.weight)
    truck.checkGoods(queueIndex)
    truckManager.sendTruckTo(truck,GoodsControlGate())
    queue.dequeue()
    goodsControlGate.occupy(truck)
  }

  private def handleGoodsCheck(truck: Truck): Unit = {
    val gateIndex = getTruckGateIndex(truck).getOrElse(throw new Exception("Truck is not in gate"))
    val goodsControlGate = goodsControlGates(gateIndex)
    val weightChecked = goodsControlGate.checkingProcess(truck)

    if (weightChecked != -1 && weightChecked < truck.weight) {
      handleWeightChecked(truck, gateIndex, weightChecked, goodsControlGate)

    } else {
      handleWeightCheckOver(truck, gateIndex, goodsControlGate)
    }
  }

  private def handleWeightChecked(truck: Truck, gateIndex: Int, weightChecked: Int, goodsControlGate: GoodsControlGate): Unit = {
    println(s"Weight Checked at gate $gateIndex: $weightChecked")
    truck.checkGoods(gateIndex, weightChecked)
    queueManager.decreaseWaitingTimes(gateIndex, 1)
  }

  private def handleWeightCheckOver(truck: Truck, gateIndex: Int, goodsControlGate: GoodsControlGate): Unit = {
    truck.status.state match {
      case GoodsCheck(gateIdx, weight) =>
        if(weight != truck.weight-1)
          println(s"Found illegal goods in ${truck.licensePlate}")
          queueManager.decreaseWaitingTimes(gateIndex, truck.weight - weight)
          truck.depart()
        else
          queueManager.decreaseWaitingTimes(gateIndex, 1)
          println(s"Truck custom cleared")
          truck.clear()
      case _ =>
    }
    goodsControlGate.release(truck)
  }

  def printStatus(): Unit = {
    status.foreach(el => print(s"${el.state} "))
    println("")
    queues(0).printr()
    queues(1).printr()
  }

  private def handleDeparture(truck: Truck): Unit = {
    truckManager.sendTruckTo(truck,OutOfSystem())
    truckList.remove(truck)
  }

  private def getTruckQueueIndex(truck: Truck): Option[Int] = {
    truck.status.state match {
      case InQueue(index, _) => Some(index)
      case _ => None
    }
  }

  private def getTruckGateIndex(truck: Truck): Option[Int] = {
    truck.status.state match {
      case GoodsCheck(index, _) => Some(index)
      case _ => None
    }
  }

  def arrive(weight: Int): Option[String] = {
    if (weight <= 0) {
      println("Invalid weight. Please provide a weight between 1 and MaxWeight")
      None
    } else {
      val truck = new CargoTruck(weight)
      truckList.enqueue(truck)
      truckManager.sendTruckTo(truck,Waiting())
      Some(truck.truckId)
    }
  }

  def step(): Unit = {
    printStatus()
    val truckListCopy = truckList.toSeq

    for (truck <- truckListCopy) {
      handleState(truck)
    }

    queueManager.optimizeQueues()
  }
  def status: List[TruckStatus] = {
    truckList.toStatus
  }

  def findStatusById(truckId: String): TruckStatus = {
    truckList.find(_.truckId == truckId).map(_.status).getOrElse(throw new NoSuchElementException(s"No status found for truckId $truckId"))
  }


  def waitingTime(truckId: String): Option[Int] = {
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

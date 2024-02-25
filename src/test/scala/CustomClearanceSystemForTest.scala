import Locations.{DocumentControlGate, GoodsControlGate, Queue}
import Traits.ControlGate
import Traits.TruckLogic.*

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class CustomClearanceSystemForTest(val queueSize:Int) {

  private val truckList: mutable.PriorityQueue[Truck] = mutable.PriorityQueue[Truck]()
  private val queues: ListBuffer[Queue] = ListBuffer(Queue(queueSize,0), Queue(queueSize,1))
  private val documentControlGate: DocumentControlGate = DocumentControlGate()
  private val goodsControlGates: ListBuffer[GoodsControlGate] = ListBuffer(GoodsControlGate(),GoodsControlGate())

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
    truck.moveTo(documentControlGate)
  }

  private def handleDocumentCheck(truck: Truck): Unit = {
    truck.status.state = if (documentControlGate.checkTruck(truck)) Staging else Departed
  }

  private def handleJoiningQueue(truck: Truck): Unit = {
    if(!queueManager.areQueuesFull) {
      val (queueIndex, waitingTime) = queueManager.add(truck)
      truck.status.state = InQueue(queueIndex, waitingTime)
      truck.moveTo(queues(queueIndex))
    }
  }


  private def handleQueueStep(truck: Truck): Unit = {
    val queueIndex = getTruckQueueIndex(truck)
    val queue = queues(queueIndex)
    val goodsControlGate = goodsControlGates(queueIndex)
    queue.peek match {
      case Some(peekTruck) if peekTruck == truck =>
        if (goodsControlGate.isGateFree) {
          truck.status.state = GoodsCheck(queueIndex)
          queue.dequeue()
          goodsControlGate.occupy()
        }
      case _ =>
    }
  }

  private def handleGoodsCheck(truck: Truck): Unit = {
    val gateIndex = getTruckGateIndex(truck)
    val goodsControlGate = goodsControlGates(gateIndex)
    val weightChecked = goodsControlGate.checkingProcess(truck)

    if (weightChecked != -1 && weightChecked<truck.weight){
      truck.status.state = GoodsCheck(gateIndex,weightChecked)
      queueManager.decreaseWaitingTimes(gateIndex,goodsControlGate.weightCheckTempo)
    } else {
      truck.status.state match {
        case GoodsCheck(gateIndex,weight) => queueManager.decreaseWaitingTimes(gateIndex,truck.weight-weight)
        case _ =>
      }
      truck.status.state = Departed
      goodsControlGate.release()
    }
  }

  private def handleDeparture(truck: Truck): Unit = {
    truck.status.location = None
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
    for (queue <- queues){
      print(queue.size)
    }
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


  private def waitingTime(id: String): Option[Int] = {
    var result: Option[Int] = None
    for (truck <- truckList) {
      if (truck.truckId == id) {
        truck.status.state match {
          case InQueue(_,waitingTime) =>
            result = Some(waitingTime)
          case _ => Error("Waiting Time not yet established")
        }
      }
    }
    result
  }
}
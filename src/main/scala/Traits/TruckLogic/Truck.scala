package Traits.TruckLogic

import Traits.Location

trait Truck extends Ordered[Truck]{
  val truckId: String
  val licensePlate: String
  val weight: Int
  val status: TruckStatus
  def moveTo(location: Location): Unit

  def compare(that: Truck): Int = {
    (this.status.state match {
      case Arrived => 5
      case DocumentCheck => 4
      case Staging => 3
      case InQueue(_,_) => 2
      case GoodsCheck(_,_) => 1
      case Departed => 0
    }) -
    (that.status.state match {
      case Arrived => 5
      case DocumentCheck => 4
      case Staging => 3
      case InQueue(_,_) => 2
      case GoodsCheck(_,_) => 1
      case Departed => 0
    })
  }

  def logEntry(): Unit = {
    status.location.logEntry(this)
  }

  def logExit(): Unit = {
    status.location.logExit(this)
  }

  def arrive(): Unit = {
    status.setState(Arrived)
  }

  def inQueue(queueNumber: Int, waitingTime: Int): Unit = {
    status.setState(InQueue(queueNumber, waitingTime))
  }

  def checkDocument(): Unit = {
    status.setState(DocumentCheck)
  }

  def checkGoods(gateNumber: Int, weightChecked: Int = 0): Unit = {
    status.setState(GoodsCheck(gateNumber, weightChecked))
  }

  def moveToStaging(): Unit = {
    status.setState(Staging)
  }

  def depart(): Unit = {
    status.setState(Departed)
  }
}

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
}

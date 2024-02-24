package Traits.TruckLogic

import Traits.Location

trait Truck {
  val truckId: String
  val licensePlate: String
  val weight: Int
  val status: TruckStatus
  def moveTo(location: Location): Unit
}

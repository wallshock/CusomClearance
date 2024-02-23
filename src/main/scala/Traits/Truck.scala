package Traits

trait Truck {
  val truckId: String
  val licensePlate: String
  val weight: Int
  val status: TruckStatus
  def moveTo(location: Location): Unit
}

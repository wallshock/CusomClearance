package Traits

trait Truck {
  val truckId: String
  val licensePlate: String
  val status: TruckStatus
  def moveTo(location: Location): Unit
}

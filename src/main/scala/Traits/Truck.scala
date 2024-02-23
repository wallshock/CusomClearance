package Traits

trait Truck {
  val licensePlate: String
  val status: TruckStatus
  def moveTo(location: Location): Unit

}

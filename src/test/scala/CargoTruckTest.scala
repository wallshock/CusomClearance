import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Traits.Location
import Traits.TruckLogic.Truck

class CargoTruckSpec extends AnyFlatSpec with Matchers {

  "A CargoTruck" should "have id" in {
    val truck = new CargoTruck(10)
    println(truck.truckId)
    truck.truckId should not be empty
  }

  "A CargoTruck" should "have a license plate" in {
    val truck = new CargoTruck(10)
    println(truck.licensePlate)
    truck.licensePlate should not be empty
  }

  it should "have a status with location None initially" in {
    val truck = new CargoTruck(5)
    truck.status.location shouldBe None
  }

  it should "update location when moved" in {
    val truck = new CargoTruck(5)
    val newLocation = new Location {
      override def getLocation: String = "New Location"
      override def logEntry(truck: Truck): Unit = {}
      override def logExit(truck: Truck): Unit = {}
    }
    truck.moveTo(newLocation)
    truck.status.location shouldBe Some("New Location")
  }
}

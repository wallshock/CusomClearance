import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Traits.{Location, Truck}

class CargoTruckSpec extends AnyFlatSpec with Matchers {

  "A CargoTruck" should "have id" in {
    val truck = new CargoTruck()
    println(truck.truckId)
    truck.truckId should not be empty
  }

  "A CargoTruck" should "have a license plate" in {
    val truck = new CargoTruck()
    println(truck.licensePlate)
    truck.licensePlate should not be empty
  }

  it should "have a status with location None initially" in {
    val truck = new CargoTruck()
    truck.status.location shouldBe None
  }

  it should "update location when moved" in {
    val truck = new CargoTruck()
    val newLocation = new Location {
      override def getLocation: String = "New Location"
      override def logEntry(truck: Truck): Unit = {}
      override def logExit(truck: Truck): Unit = {}
    }
    truck.moveTo(newLocation)
    truck.status.location shouldBe Some("New Location")
  }
}

package Locations

import Traits.Location
import Traits.TruckLogic.Truck

class InSystem extends Location {
  override def getLocation: String = "In System"

  override def logEntry(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} entered the system ${java.time.LocalDateTime.now}")
  }
  override def logExit(truck: Truck): Unit = {
    print("")
  }
}

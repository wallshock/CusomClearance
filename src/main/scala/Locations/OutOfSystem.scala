package Locations

import Traits.Location
import Traits.TruckLogic.Truck

class OutOfSystem extends Location {
  override def getLocation: String = "Departed"

  override def logEntry(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} is being departed ${java.time.LocalDateTime.now}")
  }
  override def logExit(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} successfully departed ${java.time.LocalDateTime.now}")
  }
}

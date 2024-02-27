package Locations

import Traits.Location
import Traits.TruckLogic.Truck

class OutOfSystem extends Location {
  override def getLocation: String = "Departed"

  override def logEntry(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} departed ${java.time.LocalDateTime.now}")
  }
  override def logExit(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} exited the state of being departed ${java.time.LocalDateTime.now}")
  }
}

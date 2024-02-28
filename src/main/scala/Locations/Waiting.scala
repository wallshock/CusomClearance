package Locations

import Traits.Location
import Traits.TruckLogic.Truck

class Waiting extends Location {
  override def getLocation: String = "Waiting"

  override def logEntry(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} is now waiting for action ${java.time.LocalDateTime.now}")
  }
  override def logExit(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} is no longer waiting ${java.time.LocalDateTime.now}")
  }
}
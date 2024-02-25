package Locations

import Traits.TruckLogic.TruckState
import Traits.TruckLogic.Truck
import Traits.{ControlGate, Location}

class DocumentControlGate extends ControlGate, Location {
  override def getLocation: String = "DocumentControlGate"

  override def checkTruck(truck: Truck): Boolean = {
    if(true==true) {
      //performCheck
      true
    } else {
      false
    }
  }
  
  override def logEntry(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} entered the gate at ${getLocation} at ${java.time.LocalDateTime.now}")
  }

  override def logExit(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} exited the gate at ${getLocation} at ${java.time.LocalDateTime.now}")
  }
}

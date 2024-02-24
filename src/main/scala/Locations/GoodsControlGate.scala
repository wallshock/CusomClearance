package Locations

import Traits.TruckLogic.TruckState
import Traits.TruckLogic.Truck
import Traits.{ControlGate, Location}

class GoodsControlGate extends ControlGate, Location {

  override def getLocation: String = "GoodsControlGate"
  private var gateOpen: Boolean = false //check override
  override def checkTruck(truck: Truck): Boolean = {
    true
  }
  override def isGateOpen: Boolean = {
    gateOpen
  }
  override def openGate(): Unit = {
    gateOpen = true
  }

  override def closeGate(): Unit = {
    gateOpen = false
  }

  override def logEntry(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} entered the gate at ${getLocation} at ${java.time.LocalDateTime.now}")
  }
  override def logExit(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} exited the gate at ${getLocation} at ${java.time.LocalDateTime.now}")
  }
  
}

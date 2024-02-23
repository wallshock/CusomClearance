import Traits.{ControlGate, Location, Truck}

class DocumentControlGate extends ControlGate, Location {
  override def getLocation: String = "DocumentControlGate"
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

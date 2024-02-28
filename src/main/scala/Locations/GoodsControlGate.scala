package Locations

import Traits.TruckLogic.{GoodsCheck, Truck, TruckState}
import Traits.{ControlGate, Location}

class GoodsControlGate extends ControlGate, Location {

  override def getLocation: String = "GoodsControlGate"
  private var gateFree: Boolean = true
  override def checkTruck(truck: Truck): Boolean = {
    truck.weight != 60 //sample condition to test
  }
  
  def checkingProcess(truck: Truck): Int = {
    var weightChecked = 0
    if(checkTruck(truck)){
      truck.status.state match {
        case GoodsCheck(_,value) =>
          weightChecked = value + 1
        case _ =>
      }
    } else {
      weightChecked = -1
    }
    weightChecked
  }

  
  override def logEntry(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} entered the gate ${getLocation} at ${java.time.LocalDateTime.now}")
  }
  override def logExit(truck: Truck): Unit = {
    println(s"Truck ${truck.licensePlate} exited the gate ${getLocation} at ${java.time.LocalDateTime.now}")
  }
  def isGateFree: Boolean = {
    gateFree
  }

  def occupy(truck: Truck): Unit = {
    gateFree = false
  }

  def release(truck: Truck): Unit = {
    gateFree = true
  }


}

import Traits.Location
import Traits.TruckLogic.Truck

class TruckManager {
  val logging = true
  def sendTruckTo(truck: Truck, newLocation: Location): Unit = {
    if (logging) truck.logExit() 
    truck.moveTo(newLocation)
    if (logging) truck.logEntry()
  }
  
}
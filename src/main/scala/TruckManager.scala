import Traits.Location
import Traits.TruckLogic.Truck

class TruckManager {
  def sendTruckTo(truck: Truck, newLocation: Location): Unit = {
    truck.logExit()
    truck.moveTo(newLocation)
    truck.logEntry()
  }
}
package Traits.TruckLogic

import Traits.Location
import Locations.InSystem

class TruckStatus {
  var location: Location = InSystem()
  var state: TruckState = Arrived
  
  def setState(newState: TruckState): Unit = {
    state = newState
  }
}

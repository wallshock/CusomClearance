package Traits.TruckLogic

import Traits.Location
import Locations.InSystem

class TruckStatus {
  var location: Location = InSystem()
  var state: TruckState = Arrived
  var documentCheck: Boolean = false //implement
  var goodsCheck: Boolean = false //implement
}

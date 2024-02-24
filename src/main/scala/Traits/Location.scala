package Traits

import Traits.TruckLogic.Truck

trait Location {
  def getLocation: String

  def logEntry(truck: Truck): Unit

  def logExit(truck: Truck): Unit
}

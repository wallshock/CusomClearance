package Traits

import Traits.Truck

trait ControlGate {
  def checkTruck(truck: Truck): Boolean
  def openGate(): Unit
  def closeGate(): Unit
  def isGateOpen: Boolean //UAP
  def logEntry(truck: Truck): Unit
  def logExit(truck: Truck): Unit
}
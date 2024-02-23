package Traits

trait ControlGate {
  def checkTruck(truck: Truck): Boolean
  def openGate(): Unit
  def closeGate(): Unit
  def isGateOpen: Boolean //UAP
}
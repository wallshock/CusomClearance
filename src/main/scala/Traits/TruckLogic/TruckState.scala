package Traits.TruckLogic

sealed trait TruckState

case object Arrived extends TruckState
case class InQueue(queueNumber: Int, waitingTime: Int) extends TruckState
case object DocumentCheck extends TruckState
case object GoodsCheck extends TruckState
case object Staging extends TruckState
case object Departed extends TruckState
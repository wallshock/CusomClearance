package Traits.TruckLogic

sealed trait TruckState

case object Arrived extends TruckState
case class InQueue(queueNumber:Int, waitingTime: Int) extends TruckState
case object DocumentCheck extends TruckState
case class GoodsCheck(gateNumber:Int, weightChecked: Int = 0) extends TruckState
case object Staging extends TruckState
case object Departed extends TruckState
case object CustomCleared extends TruckState
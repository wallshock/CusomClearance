import Traits.TruckLogic.*

import scala.collection.mutable.ListBuffer

object CustomClearanceSystem {

  private val truckList: ListBuffer[Truck] = ListBuffer()
  def main(args: Array[String]): Unit = {

  }

  def arrive(weight: Int): String = {
    val truck = CargoTruck(weight)
    truckList += truck
    truck.truckId
  }

  def step(): Unit = {
    truckList.foreach(truck => {
      truck.status.state match {
        case Arrived => //proccess
        case DocumentCheck => //proccess
        case Staging => //proccess
        case InQueue(_,_) => //proccess
        case GoodsCheck => //proccess
        case Departed => //proccess
      }
    })
  }
  def status: List[TruckStatus] = {
    truckList.map(truck => truck.status).toList
  }

  def waitingTime(id: String): Int = {
    //todo if in queue
    //
    //todo extract info about waiting time of given truck
    val time = 1
    time
  }
}
import Locations.{DocumentControlGate, GoodsControlGate, Queue}

import scala.collection.mutable.ArrayBuffer

class Simulation {
  def init(): Unit = {
    val controlGate = DocumentControlGate()
    val queueOne = Queue(5)
    val queueTwo = Queue(5)
    val goodsGate = GoodsControlGate()
  }

  def run(): Unit = {
    init()
    while (true) {
     //todo
    }
  }
}

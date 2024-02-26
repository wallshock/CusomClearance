import Locations.{DocumentControlGate, GoodsControlGate, Queue}

import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn
object Simulation {
  def main(args: Array[String]): Unit = {
    Simulation.run()
  }
  def run(): Unit = {
    var continue = true
    while (continue) {
      val input = StdIn.readLine()

      input match {
        case "Step" =>
          CustomClearanceSystem.step()
        case "Status" =>
          CustomClearanceSystem.status
        case number if number.toIntOption.isDefined =>
          CustomClearanceSystem.arrive(number.toInt)
        case "quit" =>
          continue = false
        case _ =>
          println("Invalid input. Please enter 'S', a number, or 'quit'.")
      }
    }
  }

}

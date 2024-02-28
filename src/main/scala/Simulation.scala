import scala.io.StdIn
object Simulation {
  def main(args: Array[String]): Unit = {
    Simulation.run()
  }
  def run(): Unit = {
    var continue = true
    println(" - - - CustomClearanceSystem - - -")
    println("Input number for arrival e.g 40")
    println("Input 's' for step")
    println("Input 'quit' for finish")

    while (continue) {
      val input = StdIn.readLine()

      input match {
        case "s" =>
          CustomClearanceSystem.step()
          
        case number if number.toIntOption.isDefined =>
          CustomClearanceSystem.arrive(number.toInt)
        case "quit" =>
          continue = false
        case _ =>
          println("Invalid input. Please enter 's', a number, or 'quit'.")
      }
    }
  }

}

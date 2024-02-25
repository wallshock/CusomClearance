import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Locations.Queue
import Traits.TruckLogic.Truck
import scala.collection.mutable.ListBuffer

class QueueManagerSpec extends AnyFlatSpec with Matchers {
  "A QueueManager" should "add a truck to the best queue" in {
    val queues = ListBuffer(Queue(5,0), Queue(5,1))
    val queueManager = new QueueManager(queues)
    val truck = new CargoTruck(5)

    val waitingTime = queueManager.add(truck)

    waitingTime should be (5)
    queues.exists(_.size == 1) should be (true)
  }
}
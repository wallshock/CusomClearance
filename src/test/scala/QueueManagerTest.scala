import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Locations.Queue
import org.scalatest.BeforeAndAfterEach

import scala.collection.immutable.List
class QueueManagerSpec extends AnyFlatSpec with Matchers with BeforeAndAfterEach {
  var queues: List[Queue] = _
  var queueManager: QueueManager = _

  override def beforeEach(): Unit = {
    queues = List(Queue(5, 0), Queue(5, 1))
    queueManager = new QueueManager(queues)
  }

  "A QueueManager" should "add a truck to the best queue" in {
    val truck = new CargoTruck(5)
    val (queue,waitingTime) = queueManager.add(truck)

    queue should be (0)
    waitingTime should be (5)
    queues.exists(_.size == 1) should be (true)
  }

  it should "optimize the queue correctly scenario 1" in {
    val queues = List(Queue(5, 0), Queue(5, 1))
    val queueManager = new QueueManager(queues)

    val truck80 = CargoTruck(80)
    val truck6 = CargoTruck(6)
    val truck19 = CargoTruck(19)
    val truck18 = CargoTruck(18)
    val truck17 = CargoTruck(17)
    val truck20_1 = CargoTruck(20)
    val truck20_2 = CargoTruck(20)

    queues.head.enqueue(truck80)
    queues.head.enqueue(truck6)
    queues.head.enqueue(truck19)
    queues.head.enqueue(truck18)
    queues.head.enqueue(truck17)

    queues(1).enqueue(truck20_1)
    queues(1).enqueue(truck20_2)

    queueManager.optimizeQueues()
    queues.head.printr()
    queues(1).printr()
    queues.head.get(0) should be(truck80)
    queues.head.get(1) should be(truck20_2)

    queues(1).get(0) should be(truck20_1)
    queues(1).get(1) should be(truck6)
    queues(1).get(2) should be(truck19)
    queues(1).get(3) should be(truck18)
    queues(1).get(4) should be(truck17)
  }

  it should "optimize the queue correctly scenario 2" in {
    val queues = List(Queue(5, 0), Queue(5, 1))
    val queueManager = new QueueManager(queues)

    val truck20_00 = CargoTruck(20)
    val truck20_01 = CargoTruck(20)
    val truck20_10 = CargoTruck(20)
    val truck20_11 = CargoTruck(20)

    queues.head.enqueue(truck20_00)
    queues.head.enqueue(truck20_01)

    queues(1).enqueue(truck20_10)
    queues(1).enqueue(truck20_11)

    queueManager.optimizeQueues()

    queues.head.get(0) should be(truck20_00)
    queues.head.get(1) should be(truck20_01)
    queues(1).get(0) should be(truck20_10)
    queues(1).get(1) should be(truck20_11)

  }

  it should "optimize the queue correctly scenario 3" in {
    val queues = List(Queue(5, 0), Queue(5, 1))
    val queueManager = new QueueManager(queues)

    val truck17 = CargoTruck(17)
    val truck8 = CargoTruck(8)
    val truck14 = CargoTruck(14)

    val truck19 = CargoTruck(19)
    val truck1 = CargoTruck(1)

    queues.head.enqueue(truck17)
    queues.head.enqueue(truck8)
    queues.head.enqueue(truck14)

    queues(1).enqueue(truck19)
    queues(1).enqueue(truck1)

    queueManager.optimizeQueues()
    queues.head.get(0) should be(truck17)
    queues.head.get(1) should be(truck1)
    queues.head.get(2) should be(truck14)


    queues(1).get(0) should be(truck19)
    queues(1).get(1) should be(truck8)
  }

  it should "optimize the queue correctly scenario 4" in {
    val queues = List(Queue(5, 0), Queue(5, 1))
    val queueManager = new QueueManager(queues)

    val truck17 = CargoTruck(17)
    val truck3 = CargoTruck(3)
    val truck14 = CargoTruck(14)

    val truck19 = CargoTruck(19)

    queues.head.enqueue(truck17)
    queues.head.enqueue(truck3)
    queues.head.enqueue(truck14)

    queues(1).enqueue(truck19)

    queueManager.optimizeQueues()

    queues.head.get(0) should be(truck17)
    queues.head.get(1) should be(truck3)

    queues(1).get(0) should be(truck19)
    queues(1).get(1) should be(truck14)
  }
  it should "optimize the queue correctly scenario 5" in {
    val queues = List(Queue(5, 0), Queue(5, 1))
    val queueManager = new QueueManager(queues)

    val truck17 = CargoTruck(17)
    val truck9 = CargoTruck(9)
    val truck1_0 = CargoTruck(1)
    val truck1_1 = CargoTruck(1)
    val truck1_2 = CargoTruck(1)

    val truck19 = CargoTruck(19)

    queues.head.enqueue(truck17)
    queues.head.enqueue(truck9)
    queues.head.enqueue(truck1_0)
    queues.head.enqueue(truck1_1)
    queues.head.enqueue(truck1_2)

    queues(1).enqueue(truck19)

    queueManager.optimizeQueues()
    queues.head.printr()
    queues(1).printr()
    println(truck17.status.state)
    println(truck9.status.state)
    println(truck1_0.status.state)
    println(truck1_1.status.state)
    println(truck1_2.status.state)
  }

  it should "optimize the queue with empty queue 1" in {
    val queues = List(Queue(5, 0), Queue(5, 1))
    val queueManager = new QueueManager(queues)

    val truck17 = CargoTruck(17)
    val truck3 = CargoTruck(3)
    val truck14 = CargoTruck(14)


    queues.head.enqueue(truck17)
    queues.head.enqueue(truck3)
    queues.head.enqueue(truck14)


    queueManager.optimizeQueues()
    queues.head.get(0) should be(truck17)

    queues(1).get(0) should be(truck3)
    queues(1).get(1) should be(truck14)
  }

  it should "optimize the queue with empty queue 0" in {
    val queues = List(Queue(5, 0), Queue(5, 1))
    val queueManager = new QueueManager(queues)

    val truck17 = CargoTruck(17)
    val truck3 = CargoTruck(3)
    val truck14 = CargoTruck(14)


    queues(1).enqueue(truck17)
    queues(1).enqueue(truck3)
    queues(1).enqueue(truck14)


    queueManager.optimizeQueues()
    queues(1).get(0) should be(truck17)
    queues.head.get(0) should be(truck3)
    queues.head.get(1) should be(truck14)
  }

  it should "optimize the queue without swapping the first elemeent" in {
    val queues = List(Queue(5, 0), Queue(5, 1))
    val queueManager = new QueueManager(queues)

    val truck3 = CargoTruck(3)
    val truck5 = CargoTruck(5)
    val truck4 = CargoTruck(4)

    queues(1).increaseGateWaitTime(15)
    queues(0).enqueue(truck3)
    queues(0).enqueue(truck5)
    queues(0).enqueue(truck4)
    queues.head.increaseGateWaitTime(60)

    queueManager.optimizeQueues()

    queues.head.get(0) should be(truck3)
    queues(1).get(0) should be(truck5)
    queues(1).get(1) should be(truck4)
  }


  it should "work with both queue empty" in {
    val queues = List(Queue(5, 0), Queue(5, 1))
    val queueManager = new QueueManager(queues)

    queueManager.optimizeQueues()
    queues.head.size should be(0)
    queues(1).size should be(0)
  }

}
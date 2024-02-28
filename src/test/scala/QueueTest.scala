import Locations.Queue
import Traits.TruckLogic.InQueue
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
class QueueSpec extends AnyFlatSpec with Matchers {
  "A Queue" should "enqueue and dequeue trucks" in {
    val queue = new Queue(2,0)
    val truck1 = new CargoTruck(5)
    val truck2 = new CargoTruck(5)

    queue.enqueue(truck1) should be (Right(()))
    queue.enqueue(truck2) should be (Right(()))

    queue.size should be (2)
    queue.peek should be (Some(truck1))

    queue.dequeue() should be (Some(truck1))
    queue.size should be (1)
    queue.peek should be (Some(truck2))

    queue.dequeue() should be (Some(truck2))
    queue.size should be (0)
    queue.peek should be (None)
  }


  it should "return None when trying to dequeue from an empty queue" in {
    val queue = new Queue(1,0)
    queue.dequeue() should be (None)
  }


  it should "throw an IndexOutOfBoundsException when an invalid index is provided" in {
    val queue = new Queue(1, 0)
    val truck = new CargoTruck(6)

    queue.enqueue(truck)

    a[IndexOutOfBoundsException] should be thrownBy {
      queue.get(-1)
    }

    a[IndexOutOfBoundsException] should be thrownBy {
      queue.get(1)
    }
  }

  it should "return the correct truck when getting an element at a valid index" in {
    val queue = new Queue(1,0)
    val truck = new CargoTruck(5)

    queue.enqueue(truck)

    queue.get(0) should be (truck)
  }

  it should "calculate the correct waiting time" in {
    val queue = new Queue(5,0)
    val element1 = new CargoTruck(5)
    val element2 = new CargoTruck(3)
    queue.enqueue(element1)
    queue.enqueue(element2)
    queue.waitingTime should be(8)
  }

  it should "calculate the correct waiting time with small Queue" in {
    val queue = new Queue(1,0)
    val element1 = new CargoTruck(5)
    val element2 = new CargoTruck(3)
    queue.enqueue(element1)
    queue.enqueue(element2)
    queue.waitingTime should be(5)
  }

  it should "return None when trying to peek at an empty queue" in {
    val queue = new Queue(1,0)
    queue.peek should be(None)
  }

  it should "calculate the correct waiting time at a certain index" in {
    val queue = new Queue(5,0)
    val element1 = new CargoTruck(5)
    val element2 = new CargoTruck(3)
    val element3 = new CargoTruck(2)
    queue.enqueue(element1)
    queue.enqueue(element2)
    queue.enqueue(element3)

    queue.waitingTimeAt(1) should be(5)
    queue.dequeue()
    queue.waitingTimeAt(1) should be(3)
    queue.dequeue()
    queue.waitingTimeAt(1) should be(2)
    queue.waitingTimeAt(2) should be(2)
    queue.waitingTimeAt(3) should be(2)
    queue.waitingTimeAt(4) should be(2)
  }

  it should "calculate the correct waiting time with different GateWayTime" in {
    val queue = new Queue(5, 0)
    val element1 = new CargoTruck(5)
    val element2 = new CargoTruck(3)
    val element3 = new CargoTruck(2)
    queue.enqueue(element1)
    queue.enqueue(element2)
    queue.enqueue(element3)

    queue.waitingTime should be(10)
    queue.dequeue()
    queue.increaseGateWaitTime(5)
    queue.waitingTime should be(10)
    queue.reduceGateCheckWaitTime(1)
    queue.waitingTime should be(9)
  }


  it should "not allow enqueue when the queue is full" in {
    val queue = new Queue(1, 0)
    val truck1 = new CargoTruck(5)
    val truck2 = new CargoTruck(3)

    queue.enqueue(truck1) should be(Right(()))
    queue.enqueue(truck2) should be(Left("Queue is full"))
  }


  it should "return None when trying to remove an element at an invalid index" in {
    val queue = new Queue(1, 0)
    val truck = new CargoTruck(5)

    queue.enqueue(truck)
    queue.removeAt(-1) should be(None)
    queue.removeAt(2) should be(None)
  }

  it should "set the truck's status to InQueue when enqueued" in {
    val queue = new Queue(3, 0)
    val truck = new CargoTruck(5)
    val truck1 = new CargoTruck(7)
    queue.increaseGateWaitTime(12)
    queue.enqueue(truck)
    queue.enqueue(truck1)

    truck.status.state match {
      case InQueue(queueIndex, waitingTime) =>
        queueIndex shouldBe 0
        waitingTime shouldBe 12
      case _ => fail("Truck status should be InQueue")
    }

    truck1.status.state match {
      case InQueue(queueIndex, waitingTime) =>
        queueIndex shouldBe 0
        waitingTime shouldBe 17
      case _ => fail("Truck status should be InQueue")
    }
  }

  it should "update the truck's status when set at an index" in {
    val queue = new Queue(3, 0)
    val truck1 = new CargoTruck(5)
    val truck2 = new CargoTruck(3)
    val truck3 = new CargoTruck(10)
    queue.increaseGateWaitTime(22)
    queue.enqueue(truck1)
    queue.setElementAt(0, truck2)
    queue.enqueue(truck3)

    truck2.status.state match {
      case InQueue(queueIndex, waitingTime) =>
        queueIndex shouldBe 0
        waitingTime shouldBe 22
      case _ => fail("Truck status should be InQueue")
    }

    truck3.status.state match {
      case InQueue(queueIndex, waitingTime) =>
        queueIndex shouldBe 0
        waitingTime shouldBe 25
      case _ => fail("Truck status should be InQueue")
    }
  }

  it should "update the enqueued truck's status when set at an index before" in {
    val queue = new Queue(3, 0)
    val truck1 = new CargoTruck(5)
    val truck2 = new CargoTruck(3)
    val truck3 = new CargoTruck(10)
    queue.increaseGateWaitTime(22)
    queue.enqueue(truck1)
    queue.setElementAt(0, truck2)
    queue.enqueue(truck3)

    truck2.status.state match {
      case InQueue(queueIndex, waitingTime) =>
        queueIndex shouldBe 0
        waitingTime shouldBe 22
      case _ => fail("Truck status should be InQueue")
    }

    truck3.status.state match {
      case InQueue(queueIndex, waitingTime) =>
        queueIndex shouldBe 0
        waitingTime shouldBe 25
      case _ => fail("Truck status should be InQueue")
    }
  }

  it should "recalculate inQueue waitingTime when gatewaytime is reduced correcly" in {
    val queue = new Queue(3, 0)
    val truck1 = new CargoTruck(10)
    queue.increaseGateWaitTime(22)
    queue.enqueue(truck1)

    truck1.status.state match {
      case InQueue(queueIndex, waitingTime) =>
        queueIndex shouldBe 0
        waitingTime shouldBe 22
      case _ => fail("Truck status should be InQueue")
    }

    queue.reduceGateCheckWaitTime(10)

    truck1.status.state match {
      case InQueue(queueIndex, waitingTime) =>
        queueIndex shouldBe 0
        waitingTime shouldBe 12
      case _ => fail("Truck status should be InQueue")
    }
  }

  it should "update the already enqueued truck's status when set at an index before" in {
    val queue = new Queue(3, 0)
    val truck1 = new CargoTruck(5)
    val truck2 = new CargoTruck(3)
    val truck3 = new CargoTruck(10)
    queue.increaseGateWaitTime(10)

    queue.enqueue(truck1)
    queue.enqueue(truck3)
    queue.setElementAt(0, truck2)


    truck3.status.state match {
      case InQueue(queueIndex, waitingTime) =>
        queueIndex shouldBe 0
        waitingTime shouldBe 13
      case _ => fail("Truck status should be InQueue")
    }

  }

  it should "update the already enqueued truck's status when removed at an index before" in {
    val queue = new Queue(3, 0)
    val truck1 = new CargoTruck(5)
    val truck2 = new CargoTruck(3)
    val truck3 = new CargoTruck(10)
    queue.increaseGateWaitTime(10)

    queue.enqueue(truck1)
    queue.enqueue(truck2)
    queue.enqueue(truck3)
    queue.removeAt(0)


    truck3.status.state match {
      case InQueue(queueIndex, waitingTime) =>
        queueIndex shouldBe 0
        waitingTime shouldBe 13
      case _ => fail("Truck status should be InQueue")
    }

  }
}
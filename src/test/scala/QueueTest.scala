import Locations.Queue
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
  
  it should "not allow more than maxSize trucks to be enqueued" in {
    val queue = new Queue(1,0)
    val truck1 = new CargoTruck(4)
    val truck2 = new CargoTruck(4)

    queue.enqueue(truck1) should be (Right(()))
    queue.enqueue(truck2) should be (Left("Queue is full"))
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
}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class QueueSpec extends AnyFlatSpec with Matchers {
  "A Queue" should "enqueue and dequeue trucks" in {
    val queue = new Queue(2)
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
    val queue = new Queue(1)
    val truck1 = new CargoTruck(4)
    val truck2 = new CargoTruck(4)

    queue.enqueue(truck1) should be (Right(()))
    queue.enqueue(truck2) should be (Left("Queue is full"))
  }

  it should "return None when trying to dequeue from an empty queue" in {
    val queue = new Queue(1)
    queue.dequeue() should be (None)
  }

  it should "return None when trying to peek at an empty queue" in {
    val queue = new Queue(1)
    queue.peek should be (None)
  }

  it should "return None when trying to get an element at an invalid index" in {
    val queue = new Queue(1)
    val truck = new CargoTruck(6)

    queue.enqueue(truck)

    queue.get(-1) should be (None)
    queue.get(1) should be (None)
  }

  it should "return the correct truck when getting an element at a valid index" in {
    val queue = new Queue(1)
    val truck = new CargoTruck(5)

    queue.enqueue(truck)

    queue.get(0) should be (Some(truck))
  }
}
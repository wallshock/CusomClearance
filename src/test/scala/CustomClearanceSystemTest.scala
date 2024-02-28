import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Locations.Queue
import Traits.TruckLogic.*
import org.scalatest.BeforeAndAfterEach



class CustomClearanceSystemSpec extends AnyFlatSpec with Matchers with BeforeAndAfterEach {
  val TestTruckWeight = 50
  var system: CustomClearanceSystemForTest = _
  var truckId: String = _

  override def beforeEach(): Unit = {
    system = CustomClearanceSystemForTest(5)
    truckId = system.arrive(TestTruckWeight)
  }

  def stepSystem(system: CustomClearanceSystemForTest, times: Int): Unit = {
    for (_ <- 1 to times) system.step()
  }

  "A CustomClearanceSystem" should "handle arrival correctly" in {
    truckId shouldBe a [String]
    truckId should not be empty
  }

  it should "transition to DocumentCheck on first step" in {
    stepSystem(system,1)
    val statusAfterFirstStep = system.status.head
    statusAfterFirstStep.state shouldBe DocumentCheck
  }

  it should "transition to Staging on second step" in {
    stepSystem(system,2)
    val statusAfterSecondStep = system.status.head
    statusAfterSecondStep.state shouldBe Staging
  }

  it should "transition to InQueue on third step" in {
    stepSystem(system,3)
    val statusAfterThirdStep = system.status.head
    statusAfterThirdStep.state shouldBe InQueue(0, 0)
  }

  it should "transition to GoodsCheck on fourth step" in {
    stepSystem(system,4)
    val statusAfterFourthStep = system.status.head
    statusAfterFourthStep.state shouldBe GoodsCheck(0,0)
  }

  it should "transition to GoodsCheck(0,1) on fifth step" in {
    stepSystem(system,5)
    val statusAfterFifthStep = system.status.head
    statusAfterFifthStep.state shouldBe GoodsCheck(0,1)
  }

  "A CustomClearanceSystem" should "handle arrival of many trucks correctly" in {
    truckId shouldBe a[String]
    truckId should not be empty
    val truckId2 = system.arrive(40)
    truckId2 shouldBe a[String]
    truckId2 should not be empty

    val statusHead = system.status.head
    val statusTail = system.status(1)
    statusHead.state shouldBe Arrived
    statusTail.state shouldBe Arrived
  }

  it should "transition to DocumentCheck on second step with two trucks" in {
    val truckId2 = system.arrive(40)
    stepSystem(system,1)
    val statusHead = system.status.head
    val statusTail = system.status(1)
    statusHead.state shouldBe DocumentCheck
    statusTail.state shouldBe DocumentCheck
  }

  it should "transition to different Queues with two trucks" in {
    val truckId2 = system.arrive(40)
    stepSystem(system,3)
    val statusHead = system.status.head
    val statusTail = system.status(1)
    statusHead.state shouldBe InQueue(0, 0)
    statusTail.state shouldBe InQueue(1, 0)
  }

  it should "transition to different GoodCheckStations with two trucks" in {
    val truckId2 = system.arrive(40)
    stepSystem(system,4)
    val statusHead = system.status.head
    val statusTail = system.status(1)
    statusHead.state shouldBe GoodsCheck(0,0)
    statusTail.state shouldBe GoodsCheck(1,0)
  }

  it should "not be able to change to GoodsCheck when they are not free" in {
    val truckId2 = system.arrive(40)
    val truckId3 = system.arrive(4)
    stepSystem(system,4)
    val status1 = system.findStatusById(truckId)
    val status2 = system.findStatusById(truckId2)
    val status3 = system.findStatusById(truckId3)
    status1.state shouldBe GoodsCheck(0,0)
    status2.state shouldBe GoodsCheck(1,0)
    status3.state shouldBe InQueue(1,40)
  }

  it should "have waitingTime decreased when waiting in Queue" in {
    val truckId2 = system.arrive(40)
    val truckId3 = system.arrive(4)
    stepSystem(system,5)
    val status1 = system.findStatusById(truckId)
    val status2 = system.findStatusById(truckId2)
    val status3 = system.findStatusById(truckId3)

    status1.state shouldBe GoodsCheck(0,1)
    status2.state shouldBe GoodsCheck(1,1)
    status3.state shouldBe InQueue(1,39)
  }

  it should "have waitingTime decreased when waiting in Queue 2" in {
    val truckId2 = system.arrive(40)
    val truckId3 = system.arrive(4)
    val truckId4 = system.arrive(6)
    stepSystem(system,6)
    val status1 = system.findStatusById(truckId)
    val status2 = system.findStatusById(truckId2)
    val status3 = system.findStatusById(truckId3)
    val status4 = system.findStatusById(truckId4)

    status1.state shouldBe GoodsCheck(0, 2)
    status2.state shouldBe GoodsCheck(1, 2)
    status3.state shouldBe InQueue(1, 38)
    status4.state shouldBe InQueue(1, 42)
  }

  it should "have waitingTime decreased when waiting in Queue 3" in {
    val truckId2 = system.arrive(40)
    val truckId3 = system.arrive(4)
    val truckId4 = system.arrive(6)
    stepSystem(system,7)
    val status1 = system.findStatusById(truckId)
    val status2 = system.findStatusById(truckId2)
    val status3 = system.findStatusById(truckId3)
    val status4 = system.findStatusById(truckId4)

    status1.state shouldBe GoodsCheck(0, 3)
    status2.state shouldBe GoodsCheck(1, 3)
    status3.state shouldBe InQueue(1, 37)
    status4.state shouldBe InQueue(1, 41)
  }

  "A CustomClearanceSystem" should "handle arrival of many trucks with small queue" in {
    val smallQueueSystem = CustomClearanceSystemForTest(1)
    val truckId1 = smallQueueSystem.arrive(50)
    val truckId2 = smallQueueSystem.arrive(40)
    val truckId3 = smallQueueSystem.arrive(4)
    val truckId4 = smallQueueSystem.arrive(6)

    stepSystem(smallQueueSystem,1)
    val status1 = smallQueueSystem.findStatusById(truckId1)
    val status2 = smallQueueSystem.findStatusById(truckId2)
    val status3 = smallQueueSystem.findStatusById(truckId3)
    val status4 = smallQueueSystem.findStatusById(truckId4)

    status1.state shouldBe DocumentCheck
    status2.state shouldBe DocumentCheck
    status3.state shouldBe DocumentCheck
    status4.state shouldBe DocumentCheck
  }

  "A CustomClearanceSystem" should "handle staging of many trucks with small queue" in {
    val smallQueueSystem = CustomClearanceSystemForTest(1)
    val truckId1 = smallQueueSystem.arrive(50)
    val truckId2 = smallQueueSystem.arrive(40)
    val truckId3 = smallQueueSystem.arrive(4)
    val truckId4 = smallQueueSystem.arrive(6)

    stepSystem(smallQueueSystem,2)
    val status1 = smallQueueSystem.findStatusById(truckId1)
    val status2 = smallQueueSystem.findStatusById(truckId2)
    val status3 = smallQueueSystem.findStatusById(truckId3)
    val status4 = smallQueueSystem.findStatusById(truckId4)

    status1.state shouldBe Staging
    status2.state shouldBe Staging
    status3.state shouldBe Staging
    status4.state shouldBe Staging
  }

  "A CustomClearanceSystem" should "handle joining the queue of many trucks with small queue" in {
    val smallQueueSystem = CustomClearanceSystemForTest(1)
    val truckId1 = smallQueueSystem.arrive(50)
    val truckId2 = smallQueueSystem.arrive(40)
    val truckId3 = smallQueueSystem.arrive(4)
    val truckId4 = smallQueueSystem.arrive(6)

    stepSystem(smallQueueSystem,3)
    val status1 = smallQueueSystem.findStatusById(truckId1)
    val status2 = smallQueueSystem.findStatusById(truckId2)
    val status3 = smallQueueSystem.findStatusById(truckId3)
    val status4 = smallQueueSystem.findStatusById(truckId4)

    status1.state shouldBe InQueue(0,0)
    status2.state shouldBe InQueue(1,0)
    status3.state shouldBe Staging
    status4.state shouldBe Staging
  }
  "A CustomClearanceSystem" should "handle queue management many trucks with small queue" in {
    val smallQueueSystem = CustomClearanceSystemForTest(1)
    val truckId1 = smallQueueSystem.arrive(50)
    val truckId2 = smallQueueSystem.arrive(40)
    val truckId3 = smallQueueSystem.arrive(4)
    val truckId4 = smallQueueSystem.arrive(6)

    stepSystem(smallQueueSystem,4)
    val status1 = smallQueueSystem.findStatusById(truckId1)
    val status2 = smallQueueSystem.findStatusById(truckId2)
    val status3 = smallQueueSystem.findStatusById(truckId3)
    val status4 = smallQueueSystem.findStatusById(truckId4)


    status1.state shouldBe GoodsCheck(0,0)
    status2.state shouldBe GoodsCheck(1,0)
    status3.state shouldBe InQueue(1,40)
    status4.state shouldBe InQueue(0,50)
  }

  "A CustomClearanceSystem" should "handle queue management many trucks with small queue 2" in {
    val smallQueueSystem = CustomClearanceSystemForTest(1)
    val truckId1 = smallQueueSystem.arrive(50)
    val truckId2 = smallQueueSystem.arrive(40)
    val truckId3 = smallQueueSystem.arrive(4)
    val truckId4 = smallQueueSystem.arrive(6)

    stepSystem(smallQueueSystem,5)
    val status1 = smallQueueSystem.findStatusById(truckId1)
    val status2 = smallQueueSystem.findStatusById(truckId2)
    val status3 = smallQueueSystem.findStatusById(truckId3)
    val status4 = smallQueueSystem.findStatusById(truckId4)

    status1.state shouldBe GoodsCheck(0, 1)
    status2.state shouldBe GoodsCheck(1, 1)
    status3.state shouldBe InQueue(1, 39)
    status4.state shouldBe InQueue(0, 49)
  }

  "A CustomClearanceSystem" should "handle rejection of Truck and adjustment of waiting times" in {
    val smallQueueSystem = CustomClearanceSystemForTest(2)
    val truckId1 = smallQueueSystem.arrive(50)
    val truckId2 = smallQueueSystem.arrive(60)
    val truckId3 = smallQueueSystem.arrive(30)
    val truckId4 = smallQueueSystem.arrive(20)
    val truckId5 = smallQueueSystem.arrive(10)
    val truckId6 = smallQueueSystem.arrive(5)
    stepSystem(smallQueueSystem, 5)
    val status1 = smallQueueSystem.findStatusById(truckId1)
    val status2 = smallQueueSystem.findStatusById(truckId2)
    val status3 = smallQueueSystem.findStatusById(truckId3)
    val status4 = smallQueueSystem.findStatusById(truckId4)
    val status5 = smallQueueSystem.findStatusById(truckId5)
    val status6 = smallQueueSystem.findStatusById(truckId6)
    
    smallQueueSystem.queuesStatus()
    status1.state shouldBe GoodsCheck(0, 1)
    status2.state shouldBe Departed
    status3.state shouldBe InQueue(0,49)
    status4.state shouldBe GoodsCheck(1,0)
    status5.state shouldBe InQueue(1,25)
    status6.state shouldBe InQueue(1,20)
  }
}

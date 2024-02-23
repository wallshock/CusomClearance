import Traits.{Location, Truck, TruckStatus}

import java.util.UUID
import scala.util.Random

class CargoTruck extends Truck {
  private def generateLicensePlate(): String = {
    val prefix = if (Random.nextBoolean()) "KR" else "KK"
    val suffix = (1 to 5).map(_ => if (Random.nextBoolean()) Random.nextInt(10) else ('A' + Random.nextInt(26)).toChar)
    prefix + suffix.mkString
  }

  private def generateId(): String = UUID.randomUUID().toString
  
  override val licensePlate: String = generateLicensePlate()
  override val truckId: String = generateId()
  override val status: TruckStatus = TruckStatus()
  override def moveTo(newLocation: Location): Unit = {
    status.location = Some(newLocation.getLocation)
  }
}

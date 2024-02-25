package Traits.TruckLogic

class TruckStatus {
  var location: Option[String] = None
  var state: TruckState = Arrived
  var documentCheck: Boolean = false //implement
  var goodsCheck: Boolean = false //implement
}

package Traits.TruckLogic

class TruckStatus {
  var location: Option[String] = None
  var state: TruckState = Arrived
  var documentCheck: Boolean = false
  var goodsCheck: Boolean = false
}

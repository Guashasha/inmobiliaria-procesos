package DTO

enum class PropertyState {AVAILABLE, OCCUPIED, SUSPENDED}

enum class PropertyType {ALL, BUILDING, HOUSE, APARTMENT, PREMISES}

enum class PropertyAction {SELL, RENT}

data class Property (
  val id: Int,
  var title: String,
  var shortDescription: String,
  var fullDescription: String,
  val type: PropertyType,
  var price: Float,
  var state: PropertyState,
  val direction: String,
  val houseOwner: Int,
  var action: PropertyAction
)

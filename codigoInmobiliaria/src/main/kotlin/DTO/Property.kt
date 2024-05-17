package DTO

import java.sql.ResultSet

enum class PropertyState {AVAILABLE, OCCUPIED, SUSPENDED}

enum class PropertyType {ALL, BUILDING, HOUSE, APARTMENT, PREMISES}

enum class PropertyAction {SELL, RENT}

data class Property (
  val id: UInt?,
  var title: String,
  var shortDescription: String,
  var fullDescription: String,
  val type: PropertyType,
  var price: Float,
  var state: PropertyState,
  val direction: String,
  val houseOwner: UInt,
  var action: PropertyAction
) {
  fun isValid (): Boolean {
    return  this.title.isNotBlank() &&
            this.shortDescription.isNotBlank() &&
            this.fullDescription.isNotBlank() &&
            this.price > 0 &&
            this.direction.isNotBlank() &&
            this.houseOwner > 0u &&
            if (this.id != null) this.id > 0u else true
  }

  companion object {
    fun fromResultSet (resultSet: ResultSet): Property {
      val id: UInt = resultSet.getInt(1).toUInt()
      val title = resultSet.getString(2)
      val shortDescription = resultSet.getString(3)
      val fullDescription = resultSet.getString(4)
      val type = PropertyType.valueOf(resultSet.getString(5))
      val price = resultSet.getFloat(6)
      val state = PropertyState.valueOf(resultSet.getString(7))
      val direction = resultSet.getString(8)
      val houseOwner = resultSet.getInt(9).toUInt()
      val action = PropertyAction.valueOf(resultSet.getString(10))

      return Property(id, title, shortDescription, fullDescription, type, price, state, direction, houseOwner, action)
    }
  }
}

package DTO

import javafx.scene.image.Image
import java.sql.ResultSet

enum class PropertyState {available, occupied, suspended}

enum class PropertyType {all, building, house, apartment, premises}

enum class PropertyAction {sell, rent}

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
  var action: PropertyAction,
  var image: Image?
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

  override fun equals (other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Property

    if (title != other.title) return false
    if (shortDescription != other.shortDescription) return false
    if (fullDescription != other.fullDescription) return false
    if (type != other.type) return false
    if (price != other.price) return false
    if (state != other.state) return false
    if (direction != other.direction) return false
    if (houseOwner != other.houseOwner) return false
    if (action != other.action) return false

    return true
  }

  override fun hashCode (): Int {
    var result = id?.hashCode() ?: 0
    result = 31 * result + title.hashCode()
    result = 31 * result + shortDescription.hashCode()
    result = 31 * result + fullDescription.hashCode()
    result = 31 * result + type.hashCode()
    result = 31 * result + price.hashCode()
    result = 31 * result + state.hashCode()
    result = 31 * result + direction.hashCode()
    result = 31 * result + houseOwner.hashCode()
    result = 31 * result + action.hashCode()
    result = 31 * result + (image?.hashCode() ?: 0)
    return result
  }

  companion object {
    fun fromResultSet (result: ResultSet): Property {
      val id: UInt = result.getInt(1).toUInt()
      val title = result.getString(2)
      val shortDescription = result.getString(3)
      val fullDescription = result.getString(4)
      val type = PropertyType.valueOf(result.getString(5))
      val price = result.getFloat(6)
      val state = PropertyState.valueOf(result.getString(7))
      val direction = result.getString(8)
      val houseOwner = result.getInt(9).toUInt()
      val action = PropertyAction.valueOf(result.getString(10))

      return Property(id, title, shortDescription, fullDescription, type, price, state, direction, houseOwner, action, null)
    }
  }
}

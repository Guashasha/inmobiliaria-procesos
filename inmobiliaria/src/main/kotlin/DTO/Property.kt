package DTO

import javafx.scene.image.Image
import java.sql.ResultSet

enum class PropertyState {available, occupied, suspended}

enum class PropertyType {all, building, house, apartment, office, retail, cabin, industrial, farm}

enum class PropertyAction {sell, rent}

data class Property (
  val id: UInt?,
  val cuv: String,
  var title: String,
  var shortDescription: String,
  var fullDescription: String,
  val type: PropertyType,
  var price: Long,
  var state: PropertyState,
  val direction: String,
  val houseOwner: UInt,
  var action: PropertyAction,
  val city: String,
  val numRooms: Int,
  val numBathrooms: Int,
  val garage: Boolean,
  val garden: Boolean,
  val size: Long,
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

    return this.cuv == other.cuv
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
      val cuv = result.getString(2)
      val title = result.getString(3)
      val shortDescription = result.getString(4)
      val fullDescription = result.getString(5)
      val type = PropertyType.valueOf(result.getString(6))
      val price = result.getLong(7)
      val state = PropertyState.valueOf(result.getString(8))
      val direction = result.getString(9)
      val houseOwner = result.getInt(10).toUInt()
      val action = PropertyAction.valueOf(result.getString(11))
      val numRooms = result.getInt(12)
      val numBathrooms = result.getInt(13)
      val garage = result.getBoolean(14)
      val garden = result.getBoolean(15)
      val city = result.getString(16)
      val size = result.getLong(17)

      return Property(id, cuv, title, shortDescription, fullDescription, type, price, state, direction, houseOwner, action, city, numRooms, numBathrooms, garage, garden, size, null)
    }
  }
}

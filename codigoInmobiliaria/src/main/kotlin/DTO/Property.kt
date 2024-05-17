package DTO

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.forEach

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
    fun fromDataRow (result: DataRow<Any?>): Property {
      val id: UInt = result[0].toString().toUInt()
      val title = result[1].toString()
      val shortDescription = result[2].toString()
      val fullDescription = result[3].toString()
      val type = PropertyType.valueOf(result[4].toString())
      val price = result[5].toString().toFloat()
      val state = PropertyState.valueOf(result[6] as String)
      val direction = result[7].toString()
      val houseOwner = result[8].toString().toUInt()
      val action = PropertyAction.valueOf(result[9].toString())
      return Property(id, title, shortDescription, fullDescription, type, price, state, direction, houseOwner, action)
    }

    fun fromDataFrame (results: DataFrame<Any?>): ArrayList<Property> {
      val properties: ArrayList<Property> = ArrayList()

      results.forEach {
        properties.add(fromDataRow(it))
      }

      return properties
    }
  }
}

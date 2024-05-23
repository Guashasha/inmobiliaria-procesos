package main.kotlin.DTO

import java.sql.ResultSet

class Query (
  val id: UInt?,
  val clientId: UInt,
  val propertyId: UInt
) {
  fun isValid (): Boolean {
    return clientId > 0u && propertyId > 0u &&
            if (this.id != null) this.id > 0u else true
  }

  companion object {
    fun fromResultSet (result: ResultSet): Query {
      val id = result.getInt(0).toUInt()
      val client = result.getInt(1).toUInt()
      val property = result.getInt(2).toUInt()

      return Query(id, client, property)
    }
  }
}
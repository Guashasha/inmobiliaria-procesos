package main.kotlin.DTO

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.forEach

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
    fun fromDataRow (result: DataRow<Any?>): Query {
      val id = result[0].toString().toUInt()
      val client = result[1].toString().toUInt()
      val property = result[2].toString().toUInt()
      return Query(id, client, property)
    }

    fun fromDataFrame (results: DataFrame<Any?>): ArrayList<Query> {
      val queries: ArrayList<Query> = ArrayList()

      results.forEach {
        queries.add(fromDataRow(it))
      }

      return queries
    }
  }
}
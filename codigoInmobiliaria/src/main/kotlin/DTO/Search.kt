package DTO

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.forEach

data class Search (
    val id: UInt?,
    val clientId: UInt,
    val propertyType: PropertyType,
    val searchTerm: String?
) {
    fun isValid () : Boolean {
        return (searchTerm?.isNotBlank() ?: true) &&
                (if (id != null) this.id > 0u else true) && this.clientId > 0u
    }

    companion object {
        fun fromDataRow (result: DataRow<Any?>): Search {
            val id: UInt = result[0].toString().toUInt()
            val clientId: UInt = result[1].toString().toUInt()
            val propertyType = PropertyType.valueOf(result[2].toString())
            val searchTerm = result[3].toString()
            return Search(id, clientId, propertyType, searchTerm)
        }

        fun fromDataFrame (results: DataFrame<Any?>): ArrayList<Search> {
            val searches: ArrayList<Search> = ArrayList()

            results.forEach {
                searches.add(fromDataRow(it))
            }

            return searches
        }
    }
}

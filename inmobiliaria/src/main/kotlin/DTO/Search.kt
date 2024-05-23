package DTO

import java.sql.ResultSet

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
        fun fromResultSet (result: ResultSet): Search {
            val id: UInt = result.getInt(0).toString().toUInt()
            val clientId: UInt = result.getInt(1).toUInt()
            val propertyType = PropertyType.valueOf(result.getString(2))
            val searchTerm = result.getString(3)

            return Search(id, clientId, propertyType, searchTerm)
        }
    }
}

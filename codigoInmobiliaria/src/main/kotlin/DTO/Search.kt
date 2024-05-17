package DTO

import java.sql.ResultSet

data class Search (
    val clientId: UInt?,
    val propertyType: PropertyType,
    val searchTerm : String
) {
    fun isVaid () : Boolean {
        return searchTerm.isNotBlank() &&
                this.clientId != null && this.clientId > 0u
    }

    companion object {
        fun fromResulSet (resultSet: ResultSet) : Search {
            val clientId : UInt = resultSet.getInt(1).toUInt()
            val searchTerm : String = resultSet.getString(2)
            val propertyType : PropertyType = PropertyType.valueOf(resultSet.getString(3))

            return Search(clientId, propertyType, searchTerm)
        }
    }
}

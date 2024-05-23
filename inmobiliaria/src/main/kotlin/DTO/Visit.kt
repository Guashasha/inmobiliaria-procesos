package main.kotlin.DTO

import java.sql.Date
import java.sql.ResultSet
import java.sql.Time

class Visit (
    val id: UInt?,
    val clientId: UInt,
    val date: Date,
    val propertyId: UInt,
    val time: Time
) {
    fun isValid () : Boolean {
        return (if (id == null) true else id > 0u) &&
                clientId > 0u && propertyId > 0u
    }

    companion object {
        fun fromResultSet (result: ResultSet): Visit {
            val id: UInt = result.getInt(0).toUInt()
            val client: UInt = result.getInt(1).toUInt()
            val date: Date = result.getDate(2)
            val property: UInt = result.getInt(3).toUInt()
            val time: Time = result.getTime(4)

            return Visit(id, client, date, property, time)
        }
    }
}
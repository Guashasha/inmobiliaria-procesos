package main.kotlin.DTO

import java.sql.Date
import java.sql.ResultSet
import java.sql.Time

class Visit (
    val id: UInt?,
    val clientId: UInt,
    val propertyId: UInt,
    val date: Date,
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
            val property: UInt = result.getInt(2).toUInt()
            val date: Date = result.getDate(4)
            val time: Time = result.getTime(5)

            return Visit(id, client, property, date, time)
        }
    }
}
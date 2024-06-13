package DTO

import java.sql.Date
import java.sql.ResultSet
import java.sql.Time

enum class VisitStatus {scheduled,cancelled,expired}

class Visit (
    val id: UInt = 0U,
    val clientId: UInt,
    val propertyId: UInt,
    var date: Date,
    var time: Time,
    var visitStatus: VisitStatus
) {
    fun isValidForAdd () : Boolean {
        return clientId > 0U && propertyId > 0U && date.toString().isNotBlank() && time.toString().isNotBlank() && visitStatus == VisitStatus.scheduled
    }

    fun isValidForEdit(): Boolean {
        return id > 0U && date.toString().isNotBlank() && time.toString().isNotBlank()
    }

    companion object {
        fun fromResultSet (result: ResultSet): Visit {
            val id: UInt = result.getInt(1).toUInt()
            val client: UInt = result.getInt(2).toUInt()
            val property: UInt = result.getInt(3).toUInt()
            val date: Date = result.getDate(4)
            val time: Time = result.getTime(5)
            val status= VisitStatus.valueOf(result.getString(6))

            return Visit(id, client, property, date, time, status)
        }
    }
}
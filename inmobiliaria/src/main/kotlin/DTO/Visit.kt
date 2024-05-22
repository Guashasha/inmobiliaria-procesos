package main.kotlin.DTO

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.forEach
import java.sql.Date
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
        fun fromDataRow (result: DataRow<Any?>): Visit {
            val id: UInt = result[0].toString().toUInt()
            val client: UInt = result[1].toString().toUInt()
            val date: Date = Date.valueOf(result[2].toString())
            val property: UInt = result[3].toString().toUInt()
            val time: Time = Time.valueOf(result[4].toString())

            return Visit(id, client, date, property, time)
        }

        fun fromDataFrame (results: DataFrame<Any?>): ArrayList<Visit> {
            val visits: ArrayList<Visit> = ArrayList()

            results.forEach {
                visits.add(fromDataRow(it))
            }

            return visits
        }
    }
}
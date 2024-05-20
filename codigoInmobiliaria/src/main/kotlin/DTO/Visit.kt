package main.kotlin.DTO

import DTO.Property
import DTO.PropertyAction
import DTO.PropertyState
import DTO.PropertyType
import org.checkerframework.checker.guieffect.qual.UI
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.getValue
import java.sql.Date
import java.sql.Time

class Visit (
    val id: UInt,
    val clientId: UInt,
    val date: Date,
    val propertyId: UInt,
    val time: Time
) {
    fun isValid () : Boolean {

    }

    companion object {
        fun fromDataRow (result: DataRow<Visit>): Visit {
            val id: UInt = result.getValue("id")
            val clientId: UInt = result.getValue("clientId")

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
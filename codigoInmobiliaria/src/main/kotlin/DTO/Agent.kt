package DTO

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.forEach


data class Agent (
    val accountId : UInt,
    val personelNumber: String

) {
    fun isValid(): Boolean {
        return this.personelNumber.isNotBlank() && this.accountId > 0u
    }

    companion object {
        fun fromDataRow (result: DataRow<Any?>): Agent {
            val id: UInt = result[0].toString().toUInt()
            val personelNumber = result[1].toString()
            return Agent(id, personelNumber)
        }

        fun fromDataFrame (results: DataFrame<Any?>): ArrayList<Agent> {
            val agents: ArrayList<Agent> = ArrayList()

            results.forEach {
                agents.add(fromDataRow(it))
            }

            return agents
        }
    }
}

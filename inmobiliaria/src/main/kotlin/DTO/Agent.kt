package DTO

import java.sql.ResultSet

data class Agent (
    val accountId : UInt,
    val personelNumber: String

) {
    fun isValid(): Boolean {
        return this.personelNumber.isNotBlank() && this.accountId > 0u
    }

    companion object {
        fun fromResultSet (result: ResultSet): Agent {
            val id: UInt = result.getInt(0).toUInt()
            val personelNumber = result.getString(1)

            return Agent(id, personelNumber)
        }
    }
}

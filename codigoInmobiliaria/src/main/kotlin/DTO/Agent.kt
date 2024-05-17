package DTO

import java.sql.ResultSet


data class Agent (
    val accountID : UInt?,
    val personelNumber: String

) {
    fun isValid(): Boolean {
        return this.personelNumber.isNotBlank() &&
                this.accountID != null && this.accountID > 0u
    }

    companion object {
        fun fromResulSet (resultSet: ResultSet) : Agent {
            val accountID : UInt = resultSet.getInt(1).toUInt()
            val personelNumber : String = resultSet.getString(2)

            return Agent(accountID, personelNumber)
        }
    }
}

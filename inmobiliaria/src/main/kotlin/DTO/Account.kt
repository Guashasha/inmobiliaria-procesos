package DTO

import java.sql.ResultSet
import java.util.*

enum class AccountType { CLIENT, AGENT }

data class Account(
    val id: UInt?,
    val email: String,
    val type: AccountType,
    val name: String,
    val lastName: String,
    val phone: String,
    val password: String?,
) {
    fun isValid(): Boolean {
        return this.email.isNotBlank() &&
                this.phone.isNotBlank() &&
                this.name.isNotBlank() &&
                this.lastName.isNotBlank() &&
                (if (this.id != null) this.id > 0u else true) &&
                if (this.password != null) this.password.isNotBlank() else true
    }

    companion object {
        fun fromResultSet (result: ResultSet): Account {
            val id: UInt = result.getInt(1).toUInt()
            val name = result.getString(2)
            val lastName = result.getString(3)
            val type = AccountType.valueOf(result.getString(4).uppercase(Locale.getDefault()))
            val email = result.getString(5)
            val phone = result.getString(6)
            val password = result.getString(7)

            return Account(id, email, type, name, lastName, phone, password)
        }
    }
}


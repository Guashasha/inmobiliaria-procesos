package DTO

import java.sql.ResultSet
import java.util.*

enum class AccountType { CLIENT, AGENT }

data class Account(
    val id: UInt?,
    val email: String,
    val type: AccountType,
    val name: String,
    val phone: String,
    val password: String?,
) {
    fun isValid(): Boolean {
        return this.email.isNotBlank() &&
                this.phone.isNotBlank() &&
                this.name.isNotBlank() &&
                (if (this.id != null) this.id > 0u else true) &&
                if (this.password != null) this.password.isNotBlank() else true
    }

    companion object {
        fun fromResultSet (result: ResultSet): Account {
            val id: UInt = result.getInt(1).toUInt()
            val name = result.getString(2)
            val type = AccountType.valueOf(result.getString(3).uppercase(Locale.getDefault()))
            val email = result.getString(4)
            val phone = result.getString(5)

            return Account(id, email, type, name, phone, null)
        }
    }
}


package DTO

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.forEach

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
        fun fromDataRow (result: DataRow<Any?>): Account {
            val id: UInt = result[0].toString().toUInt()
            val name = result[1].toString()
            val type = AccountType.valueOf(result[2].toString())
            val email = result[3].toString()
            val phone = result[4].toString()
            return Account(id, email, type, name, phone, null)
        }

        fun fromDataFrame (results: DataFrame<Any?>): ArrayList<Account> {
            val accounts: ArrayList<Account> = ArrayList()

            results.forEach {
                accounts.add(fromDataRow(it))
            }

            return accounts
        }
    }
}


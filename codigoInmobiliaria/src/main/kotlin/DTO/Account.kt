package DTO

import java.sql.Blob
import java.sql.ResultSet

enum class AccountType { CLIENT, AGENT }

data class Account(
    val id: UInt?,
    val email: String,
    val type: AccountType,
    val name: String,
    val password: String,
    val phone: String,
    val profileImage: Blob?

) {
    fun isValid(): Boolean {
        return this.email.isNotBlank() &&
                this.password.isNotBlank() &&
                this.phone.isNotBlank() &&
                this.profileImage != null &&
                isBlobNotEmpty(this.profileImage)
    }


    private fun isBlobNotEmpty(blob: Blob): Boolean {
        return blob.length() > 0
    }

    companion object {
        fun fromResulset(resultSet: ResultSet): Account {
            val id: UInt = resultSet.getInt(1).toUInt()
            val name: String = resultSet.getString(2)
            val type = AccountType.valueOf(resultSet.getString(3))
            val email: String = resultSet.getString(4)
            val phone: String = resultSet.getString(5)
            val password: String = resultSet.getString(6)
            val profileImage: Blob? = resultSet.getBlob(7)

            return Account(id, email, type, name, password, phone, profileImage)
        }
    }
}


package DTO

import javafx.scene.image.Image
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.forEach
import java.sql.Blob

enum class AccountType { CLIENT, AGENT }

data class Account(
    val id: UInt?,
    val email: String,
    val type: AccountType,
    val name: String,
    val phone: String,
    val profileImageBytes: ByteArray?

) {
    fun isValid(): Boolean {
        return this.email.isNotBlank() &&
                this.phone.isNotBlank() &&
                this.name.isNotBlank() &&
                this.profileImageBytes != null &&
                if (this.id != null) this.id > 0u else true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Account

        if (id != other.id) return false
        if (email != other.email) return false
        if (type != other.type) return false
        if (name != other.name) return false
        if (phone != other.phone) return false
        if (profileImageBytes != null) {
            if (other.profileImageBytes == null) return false
            if (!profileImageBytes.contentEquals(other.profileImageBytes)) return false
        } else if (other.profileImageBytes != null) return false

        return true
    }

    companion object {
        fun fromDataRow (result: DataRow<Any?>): Account {
            val id: UInt = result[0].toString().toUInt()
            val email = result[1].toString()
            val type = AccountType.valueOf(result[2].toString())
            val name = result[3].toString()
            val phone = result[4].toString()
            val profilePicture = result[5] as ByteArray
            return Account(id, email, type, name, phone, profilePicture)
        }

        fun fromDataFrame (results: DataFrame<Any?>): ArrayList<Account> {
            val agents: ArrayList<Account> = ArrayList()

            results.forEach {
                agents.add(fromDataRow(it))
            }

            return agents
        }
    }
}


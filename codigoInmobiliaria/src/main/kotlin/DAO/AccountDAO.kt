package main.kotlin.DAO

import DTO.Account
import DataAccess.DataBaseConnection
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import java.sql.SQLException

sealed class AccountResult (val message: String) {
    class Success: AccountResult("La operación se realizó correctamente")
    class Failure: AccountResult("La operación no se pudo realizar")
    class FoundList(val accounts: List<Account>): AccountResult("Se encontraron multiples cuentas")
    class Found(val account: Account): AccountResult("Se encontró la cuenta buscada")
    class NotFound: AccountResult("La cuenta a buscar no existe")
    class DBError(private val errorMessage: String): AccountResult(errorMessage)
    class WrongAccount: AccountResult("Los datos de la cuenta son incorrectos")
}

class AccountDAO {
    private val dbConnection = DataBaseConnection().connection

    fun add (account: Account): AccountResult {
        if (!account.isValid() || account.password == null) {
            return AccountResult.WrongAccount()
        }

        return try {
            val query =
                dbConnection.prepareStatement("INSERT INTO account (name, type, email, phone, password) VALUES (?, ?, ?, ?, ?, ?);")
            query.setString(1, account.name)
            query.setString(2, account.type.toString())
            query.setString(3, account.email)
            query.setString(4, account.phone)
            query.setString(5, account.password)

            if (query.executeUpdate() > 0) {
                AccountResult.Success()
            } else {
                AccountResult.Failure()
            }
        }
        catch (error: SQLException) {
            AccountResult.DBError(error.message.toString())
        }
    }

    // fun modify (account: Account): AccountResult {}

    fun getById (accountId: UInt): AccountResult {
        val query = "SELECT id, name, type, email, phone FROM account WHERE id=${accountId};"
        val result = DataFrame.readSqlQuery(dbConnection, query)

        return if (result.isEmpty()) {
            AccountResult.NotFound()
        } else {
            AccountResult.Found(Account.fromDataRow(result.first()))
        }
    }

    fun getAll (): AccountResult {
        val result = DataFrame.readSqlTable(dbConnection, "account")

        return AccountResult.FoundList(Account.fromDataFrame(result))
    }
}

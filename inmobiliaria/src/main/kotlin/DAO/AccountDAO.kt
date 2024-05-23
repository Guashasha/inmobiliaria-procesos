package DAO

import DTO.Account
import DataAccess.DataBaseConnection
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
                dbConnection.prepareStatement("INSERT INTO account (name, type, email, phone, password) VALUES (?, ?, ?, ?, ?);")
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

    fun modify (account: Account): AccountResult {
        if (!account.isValid()) {
            return AccountResult.WrongAccount()
        }
        else if (account.id == null) {
            return AccountResult.NotFound()
        }

        return try {
            val query =
                dbConnection.prepareStatement("UPDATE account SET name=?, type=?, email=?, phone=?, password=? where id=?;")

            query.setString(1, account.name)
            query.setString(2, account.type.toString())
            query.setString(3, account.email)
            query.setString(4, account.phone)
            query.setString(5, account.password)
            query.setInt(6, account.id.toInt())

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

    fun getById (accountId: UInt): AccountResult {
        if (accountId < 1u) {
            return AccountResult.WrongAccount()
        }

        return try {
            val query = dbConnection.prepareStatement("SELECT * FROM account WHERE id=?;")

            query.setInt(1, accountId.toInt())

            val result = query.executeQuery()

            if (result.next()) {
                AccountResult.Found(Account.fromResultSet(result))
            } else {
                AccountResult.NotFound()
            }
        }
        catch (error: SQLException) {
            AccountResult.DBError(error.message.toString())
        }
    }

    fun getAll (): AccountResult {
        val result = dbConnection.prepareStatement("SELECT * FROM account;").executeQuery()
        val list = ArrayList<Account>()

        while (result.next()) {
            list.add(Account.fromResultSet(result))
        }

        return if (list.isNotEmpty()) {
            AccountResult.FoundList(list)
        }
        else {
            AccountResult.NotFound()
        }
    }

    fun getByEmail(email: String): AccountResult {
        if (email.isBlank()) {
            return AccountResult.WrongAccount()
        }

        return try {
            val query = dbConnection.prepareStatement("SELECT * FROM account WHERE email = ?;")
            query.setString(1, email)

            val result = query.executeQuery()

            if (result.next()) {
                AccountResult.Found(Account.fromResultSet(result))
            } else {
                AccountResult.NotFound()
            }
        } catch (error: SQLException) {
            AccountResult.DBError(error.message.toString())
        }
    }


}

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
    fun add (account: Account): AccountResult {
        if (!account.isValid() || account.password == null) {
            return AccountResult.WrongAccount()
        }
        val emailResult = getByEmail(account.email)
        if (emailResult is AccountResult.Found) {
            return AccountResult.Failure()
        }

        return try {
            val dbConnection = DataBaseConnection().connection

            val query =
                dbConnection.prepareStatement("INSERT INTO account (name, lastName, type, email, phone, password) VALUES (?, ?, ?, ?, ?, ?);")
            query.setString(1, account.name)
            query.setString(2, account.lastName)
            query.setString(3, account.type.toString().lowercase())
            query.setString(4, account.email)
            query.setString(5, account.phone)
            query.setString(6, account.password)

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
        val emailResult = getByEmailDistinctId(account)
        if (emailResult is AccountResult.Found) {
            return AccountResult.Failure()
        }

        return try {
            val dbConnection = DataBaseConnection().connection

            val query =
                dbConnection.prepareStatement("UPDATE account SET name=?, lastname =?, type=?, email=?, phone=?, password=? where id=?;")

            query.setString(1, account.name)
            query.setString(2, account.lastName)
            query.setString(3, account.type.toString().lowercase())
            query.setString(4, account.email)
            query.setString(5, account.phone)
            query.setString(6, account.password)
            query.setInt(7, account.id.toInt())

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

    fun delete(account: Account): AccountResult {
        if (!account.isValid()) {
            return AccountResult.WrongAccount()
        } else if (account.id == null) {
            return AccountResult.NotFound()
        }

        return try {
            val dbConnection = DataBaseConnection().connection
            dbConnection.autoCommit = false

            try {
                val deleteVisitsQuery = dbConnection.prepareStatement("DELETE FROM visit WHERE clientId = ?;")
                deleteVisitsQuery.setInt(1, account.id.toInt())
                deleteVisitsQuery.executeUpdate()

                val deleteAccountQuery = dbConnection.prepareStatement("DELETE FROM account WHERE id = ?;")
                deleteAccountQuery.setInt(1, account.id.toInt())

                if (deleteAccountQuery.executeUpdate() > 0) {
                    dbConnection.commit()
                    AccountResult.Success()
                } else {
                    dbConnection.rollback()
                    AccountResult.Failure()
                }
            } catch (error: SQLException) {
                dbConnection.rollback()
                AccountResult.DBError(error.message.toString())
            } finally {
                dbConnection.autoCommit = true
            }
        } catch (error: SQLException) {
            AccountResult.DBError(error.message.toString())
        }
    }



    fun getById (accountId: UInt): AccountResult {
        if (accountId < 1u) {
            return AccountResult.WrongAccount()
        }

        return try {
            val dbConnection = DataBaseConnection().connection

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
        val dbConnection = DataBaseConnection().connection

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

    fun getByEmailDistinctId(account: Account): AccountResult {
        if (account.id == null) {
            return AccountResult.WrongAccount()
        }

        return try {
            val dbConnection = DataBaseConnection().connection

            val query = dbConnection.prepareStatement("SELECT * FROM account WHERE email = ? AND id != ?;")
            query.setString(1, account.email)
            query.setInt(2, account.id.toInt())

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

    fun getByEmail(email: String): AccountResult {
        if (email.isBlank()) {
            return AccountResult.WrongAccount()
        }

        return try {
            val dbConnection = DataBaseConnection().connection

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

    fun validateCredentials(email: String, password: String): AccountResult {
        if (email.isBlank() || password.isBlank()) {
            return AccountResult.WrongAccount()
        }

        return try {
            val dbConnection = DataBaseConnection().connection

            val query = dbConnection.prepareStatement("SELECT * FROM account WHERE email = ? AND BINARY password = BINARY ?;")
            query.setString(1, email)
            query.setString(2, password)

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

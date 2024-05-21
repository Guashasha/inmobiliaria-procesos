package main.kotlin.DAO

import DTO.Account

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
    // fun add (account: Account): AccountResult {}
    // fun modify (account: Account): AccountResult {}
    // fun getById (accountId: UInt): AccountResult {}
    // fun getAll (): AccountResult {}
    // fun delete (accountId: UInt): AccountResult {}
}

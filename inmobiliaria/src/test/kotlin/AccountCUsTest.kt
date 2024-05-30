import DAO.AccountDAO
import DAO.AccountResult
import DTO.*
import DataAccess.DataBaseConnection
import main.kotlin.DAO.PropertyDAO
import main.kotlin.DAO.PropertyResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class AccountCUsTest {
    private val accountDAO = AccountDAO()

    @Test
    fun createClientAccount() {
        val account = Account(null, "john@example.com", AccountType.CLIENT, "meow", "1234567890", "password")
        val result = accountDAO.add(account)
        assertTrue(result is AccountResult.Success)
    }

    @Test
    fun testAddInvalidAccount() {
        val account = Account(null, "fer@hotmail.com", AccountType.CLIENT, "Edgar tripalosvski", "1234567890", "password")
        val result = accountDAO.add(account)
        assertTrue(result is AccountResult.Failure)
    }

    @Test
    fun testEmptyFields () {
        val account = Account(null, "", AccountType.CLIENT, "", "", "")
        val result = accountDAO.add(account)
        assertTrue(result is AccountResult.WrongAccount)
    }

    @Test
    fun testModifySuccessful () {
        val account = Account(3u, "John Doe", AccountType.CLIENT, "john@example.com", "1234567890", "password")
        val result = accountDAO.modify(account)
        assertTrue(result is AccountResult.Success)
    }

    @Test
    fun testModifyDuplicateEmail () {
        val account = Account(3u, "pale@hotmail.com", AccountType.CLIENT, "Hernan", "1234567890", "password")
        val result = accountDAO.modify(account)
        assertTrue(result is AccountResult.Failure)
    }



    companion object {
        @BeforeEach
        fun setUp(): Unit {
            TestHelper.addTestData()
        }
    }
}
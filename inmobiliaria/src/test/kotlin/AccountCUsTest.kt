import DAO.AccountDAO
import DAO.AccountResult
import DTO.*
import DataAccess.DataBaseConnection
import main.kotlin.DAO.PropertyDAO
import main.kotlin.DAO.PropertyResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class AccountCUsTest {
    private val accountDAO = AccountDAO()

    @Test
    fun createClientAccount() {
        val account = Account(null, "John Doe", AccountType.CLIENT, "john@example.com", "1234567890", "password")
        val result = accountDAO.add(account)
        assertTrue(result is AccountResult.Success)
    }

    @Test
    fun testAddInvalidAccount() {
        val account = Account(null, "", AccountType.CLIENT, "john@example.com", "1234567890", "password")
        val result = accountDAO.add(account)
        assertTrue(result is AccountResult.WrongAccount)
    }



    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp(): Unit {
            TestHelper.addTestData()
        }
    }
}
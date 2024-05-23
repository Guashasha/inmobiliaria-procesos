package DataAccess

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class DataBaseConnectionTest {
    @Test
    fun getConnectionSuccesfull () {
        val dataBaseConnection = DataBaseConnection()
        assertNotNull(dataBaseConnection.connection)
    }
}
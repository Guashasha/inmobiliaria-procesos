package DataAccess

import java.sql.Connection
import java.sql.DriverManager

class DataBaseConnection {
    private val url = "jdbc:mariadb://localhost:3306/PXNGAgency"
    private val username = "clientePXNG"
    private val password = "papuCOIL"

    internal val connection: Connection = DriverManager.getConnection(url, username, password)
}
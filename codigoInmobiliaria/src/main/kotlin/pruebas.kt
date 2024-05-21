import DAO.PropertyResult
import DTO.Property
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import DataAccess.DataBaseConnection
import org.jetbrains.kotlinx.dataframe.api.firstOrNull

fun main () {
    val dbConnection = DataBaseConnection().connection
    val propertyId = 1
    val query = "SELECT * FROM property WHERE id=${propertyId}"
    val result = DataFrame.readSqlQuery(dbConnection, query)
    val row = result.firstOrNull()

    if (row != null) {
        print(Property.fromDataRow(row).toString())
    }
    else {
        print("no hay entradas")
    }
}
package main.kotlin.DAO

import DAO.PropertyResult
import DTO.Property
import DataAccess.DataBaseConnection
import main.kotlin.DTO.Query
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import java.sql.SQLException

sealed class QueryResult (val message: String) {
    class Success: QueryResult("La operación se realizó correctamente")
    class Failure: QueryResult("La operación no se pudo realizar")
    class FoundList(val queries: List<Query>): QueryResult("Se encontraron multiples busquedas")
    class Found(val query: Query): QueryResult("Se encontró la busqueda")
    class NotFound: QueryResult("La busqueda a buscar no existe")
    class DBError(private val errorMessage: String): QueryResult(errorMessage)
    class WrongQuery: QueryResult("Los datos de la busqueda son incorrectos")
}

class QueryDAO {
    private val dbConnection = DataBaseConnection().connection

     fun add (query: Query): QueryResult {
         if (!query.isValid()) {
             return QueryResult.WrongQuery()
         }

         return try {
             val dbQuery = dbConnection.prepareStatement("INSERT INTO query (clientId, propertyId) VALUES (?, ?);")

             dbQuery.setInt(1, query.clientId.toInt())
             dbQuery.setInt(2, query.propertyId.toInt())

             if (dbQuery.executeUpdate() > 0) {
                 QueryResult.Success()
             } else {
                 QueryResult.Failure()
             }
         }
         catch (error: SQLException) {
             QueryResult.DBError(error.message.toString())
         }
     }

     fun getById (queryId: UInt): QueryResult {
         if (queryId < 1u) {
             return QueryResult.NotFound()
         }

         val dbQuery = "SELECT * FROM property WHERE id=${queryId};"
         val result = DataFrame.readSqlQuery(dbConnection, dbQuery)

         return if (result.isEmpty()) {
             QueryResult.NotFound()
         } else {
             QueryResult.Found(Query.fromDataRow(result.first()))
         }
     }

     fun getAll (): QueryResult {
         val result = DataFrame.readSqlTable(dbConnection, "property")

         return QueryResult.FoundList(Query.fromDataFrame(result))
     }

     fun delete (queryId: UInt): QueryResult {
         if (queryId < 1u) {
             return QueryResult.WrongQuery()
         }

         val dbQuery = dbConnection.prepareStatement("DELETE FROM query WHERE id=${queryId};")

         return try {
             if (dbQuery.executeUpdate() < 1) {
                 QueryResult.Failure()
             }
             else {
                 QueryResult.Success()
             }
         }
         catch (error: SQLException) {
             QueryResult.DBError(error.message.toString())
         }
     }
}

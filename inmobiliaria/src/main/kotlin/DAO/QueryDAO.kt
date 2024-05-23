package DAO

import DataAccess.DataBaseConnection
import main.kotlin.DTO.Query
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

         return try {
             val dbQuery = "SELECT * FROM property WHERE id=${queryId};"
             val result = dbConnection.prepareStatement(dbQuery).executeQuery()

             if (result.next()) {
                 QueryResult.Found(Query.fromResultSet(result))
             } else {
                 QueryResult.NotFound()
             }
         }
         catch (error: SQLException) {
             QueryResult.DBError(error.message.toString())
         }
     }

     fun getAll (): QueryResult {
         return try {
             val result = dbConnection.prepareStatement("SELECT * FROM query;").executeQuery()
             val list = ArrayList<Query>()

             while (result.next()) {
                 list.add(Query.fromResultSet(result))
             }

             if (list.isNotEmpty()) {
                 QueryResult.FoundList(list)
             } else {
                 QueryResult.NotFound()
             }
         }
         catch (error: SQLException) {
             QueryResult.DBError(error.message.toString())
         }
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

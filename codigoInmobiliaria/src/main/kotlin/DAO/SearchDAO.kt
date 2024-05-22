package main.kotlin.DAO

import DTO.Search
import DataAccess.DataBaseConnection
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.isEmpty
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import java.sql.SQLException

sealed class SearchResult (val message: String) {
    class Success: SearchResult("La operación se realizó correctamente")
    class Failure: SearchResult("La operación no se pudo realizar")
    class FoundList(val searches: List<Search>): SearchResult("Se encontraron multiples visitas")
    class Found(val search: Search): SearchResult("Se encontró la visita buscada")
    class NotFound: SearchResult("La visita a buscar no existe")
    class DBError(private val errorMessage: String): SearchResult(errorMessage)
    class WrongSearch: SearchResult("Los datos de la visita son incorrectos")
}

class SearchDAO {
    private val dbConnection = DataBaseConnection().connection

    fun add (search: Search): SearchResult {
        if (!search.isValid()) {
            return SearchResult.WrongSearch()
        }

        return try {
            val query = dbConnection.prepareStatement("INSERT INTO search (title, shortDescription, fullDescription, type, price, state, direction, houseOwner, action) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);")

            query.setInt(1, search.clientId.toInt())
            query.setString(2, search.propertyType.toString())
            query.setString(3, search.searchTerm)

            if (query.executeUpdate() > 0) {
                SearchResult.Success()
            } else {
                SearchResult.Failure()
            }
        }
        catch (error: SQLException) {
            SearchResult.DBError(error.message.toString())
        }
    }

    fun getById (searchId: UInt): SearchResult {
        if (searchId < 1u) {
            return SearchResult.WrongSearch()
        }

        val query = "SELECT id, clientId, propertyType, searchTerm FROM search WHERE id=?;"
        val result = DataFrame.readSqlQuery(dbConnection, query)

        return if (result.isEmpty()) {
            SearchResult.NotFound()
        }
        else SearchResult.Found(Search.fromDataRow(result.first()))
    }

    fun getAll (): SearchResult {
        val result = DataFrame.readSqlTable(dbConnection, "search")

        return SearchResult.FoundList(Search.fromDataFrame(result))
    }

    fun delete (searchId: UInt): SearchResult {
       if (searchId < 1u) {
           return SearchResult.WrongSearch()
       }

        return try {
            val query = dbConnection.prepareStatement("DELETE FROM search WHERE id=?;")

            query.setInt(1, searchId.toInt())

            if (query.executeUpdate() < 1) {
                SearchResult.Failure()
            }
            else {
                SearchResult.Success()
            }
        }
        catch (error: SQLException) {
            SearchResult.DBError(error.message.toString())
        }
    }
}

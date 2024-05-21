package main.kotlin.DAO

import main.kotlin.DTO.Query

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
    // fun add (query: Query): QueryResult {}
    // fun modify (query: Query): QueryResult {}
    // fun getById (queryId: UInt): QueryResult {}
    // fun getAll (): QueryResult {}
    // fun delete (queryId: UInt): QueryResult {}
}

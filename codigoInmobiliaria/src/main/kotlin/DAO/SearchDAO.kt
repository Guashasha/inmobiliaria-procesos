package main.kotlin.DAO

import DTO.Search

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
    // fun add (search: Search): SearchResult {}
    // fun modify (search: Search): SearchResult {}
    // fun getById (searchId: UInt): SearchResult {}
    // fun getAll (): SearchResult {}
    // fun delete (searchId: UInt): SearchResult {}
}

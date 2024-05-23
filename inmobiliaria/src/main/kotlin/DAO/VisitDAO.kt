package DAO

import DTO.Property
import DataAccess.DataBaseConnection
import main.kotlin.DTO.Visit

sealed class VisitResult (val message: String) {
    class Success: VisitResult("La operación se realizó correctamente")
    class Failure: VisitResult("La operación no se pudo realizar")
    class FoundList(val visits: List<Visit>): VisitResult("Se encontraron multiples visitas")
    class Found(val visit: Visit): VisitResult("Se encontró la visita buscada")
    class NotFound: VisitResult("La visita a buscar no existe")
    class DBError(private val errorMessage: String): VisitResult(errorMessage)
    class WrongVisit: VisitResult("Los datos de la visita son incorrectos")
}

class VisitDAO {
    val dbConnection = DataBaseConnection().connection
    fun add (visit: Visit): VisitResult {
return VisitResult.WrongVisit()
    }
    // fun modify (visit: Visit): VisitResult {}
    // fun getById (visitId: UInt): VisitResult {}
    // fun getAll (): VisitResult {}
    // fun delete (visitId: UInt): VisitResult {}
}
package DAO

import DTO.Property
import DataAccess.DataBaseConnection
import main.kotlin.DAO.PropertyResult
import main.kotlin.DTO.Visit
import org.checkerframework.checker.guieffect.qual.UI
import java.sql.Date
import java.sql.SQLException
import java.sql.Time

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

    fun getUnavailableVisits (idProperty : UInt, date : Date) : VisitResult {
        if (idProperty == 0U) return VisitResult.Failure()

        val unavailableVisitsList = ArrayList<Visit>()

        return try {
            val query = dbConnection.prepareStatement("SELECT * FROM visit WHERE propertyid = ? AND DATE = ?")
            query.setInt(1,idProperty.toInt())
            query.setDate(2,date)

            var result = query.executeQuery()

            while (result.next()) unavailableVisitsList.add(Visit.fromResultSet(result))

            VisitResult.FoundList(unavailableVisitsList)
        }
        catch (error: SQLException) {
            VisitResult.DBError(error.message.toString())
        }
    }

    fun add (visit: Visit): VisitResult {
        if (!visit.isValidForAdd()) return VisitResult.Failure()

        return try {
            val query = dbConnection.prepareStatement("INSERT INTO visit (clientId,propertyId,DATE,TIME) VALUES (?,?,?,?)")
            query.setInt(1,visit.clientId.toInt())
            query.setInt(2,visit.propertyId.toInt())
            query.setDate(3,visit.date)
            query.setTime(4,visit.time)

            if (query.executeUpdate() > 0) VisitResult.Success() else VisitResult.WrongVisit()
        }
        catch (error: SQLException) {
            VisitResult.DBError(error.message.toString())
        }
    }
    // fun modify (visit: Visit): VisitResult {}
    // fun getById (visitId: UInt): VisitResult {}
    // fun getAll (): VisitResult {}
    // fun delete (visitId: UInt): VisitResult {}
}
package DAO

import DataAccess.DataBaseConnection
import main.kotlin.DTO.Visit
import java.sql.Date
import java.sql.SQLException

sealed class VisitResult (val message: String) {
    class Success: VisitResult("La operación se realizó correctamente")
    class Failure: VisitResult("La operación no se pudo realizar")
    class FoundList(val visits: List<Visit>): VisitResult("Se encontraron multiples visitas")
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
            val query = dbConnection.prepareStatement("INSERT INTO visit (clientId,propertyId,date,time) VALUES (?,?,?,?)")
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
}
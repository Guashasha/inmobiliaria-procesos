package DAO

import DataAccess.DataBaseConnection
import main.kotlin.DTO.Visit
import java.sql.Date
import java.sql.SQLException

sealed class VisitResult (val message: String) {
    class Success(successMessage: String): VisitResult(successMessage)
    class Failure: VisitResult("La operación no se pudo realizar")
    class FoundList(val visits: List<Visit>): VisitResult("Se encontraron multiples visitas")
    class FoundVisit(val visit: Visit): VisitResult("Se encontró la visita")
    class NotFound: VisitResult("La visita a buscar no existe")
    class DBError(errorMessage: String): VisitResult(errorMessage)
    class WrongVisit: VisitResult("Ha ocurrido un error al agendar la visita")
}

class VisitDAO {
    private val dbConnection = DataBaseConnection().connection

    fun getUnavailableVisits (idProperty : UInt, date : Date) : VisitResult {
        if (idProperty == 0U) return VisitResult.Failure()

        val unavailableVisitsList = ArrayList<Visit>()

        return try {
            val query = dbConnection.prepareStatement("SELECT * FROM visit WHERE propertyid = ? AND date = ? AND visitStatus = 'scheduled'")
            query.setInt(1,idProperty.toInt())
            query.setDate(2,date)

            val result = query.executeQuery()

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
            val query = dbConnection.prepareStatement("INSERT INTO visit (clientId,propertyId,date,time,visitStatus) VALUES (?,?,?,?,?)")
            query.setInt(1,visit.clientId.toInt())
            query.setInt(2,visit.propertyId.toInt())
            query.setDate(3,visit.date)
            query.setTime(4,visit.time)
            query.setString(5,visit.visitStatus.toString())

            if (query.executeUpdate() > 0) VisitResult.Success("La visita se ha agendado correctamente") else VisitResult.WrongVisit()
        }
        catch (error: SQLException) {
            VisitResult.DBError(error.message.toString())
        }
    }

    fun edit (visit: Visit): VisitResult {
        if (!visit.isValidForEdit()) return VisitResult.Failure()

        return try {
            val query = dbConnection.prepareStatement("UPDATE visit SET date = ?, time = ?, visitStatus = ? WHERE id = ?")
            query.setDate(1,visit.date)
            query.setTime(2,visit.time)
            query.setString(3,visit.visitStatus.toString())
            query.setInt(4,visit.id.toInt())

            if (query.executeUpdate() > 0) VisitResult.Success("La visita se ha reagendado correctamente") else VisitResult.WrongVisit()
        }
        catch (error: SQLException) {
            VisitResult.DBError(error.message.toString())
        }
    }

    fun getVisit (idClient: UInt, idProperty: UInt): VisitResult {
        if (idClient == 0U || idProperty == 0U) return VisitResult.Failure()

        return try {
            val query = dbConnection.prepareStatement("SELECT * FROM visit WHERE clientId = ? AND propertyId = ? AND visitStatus = 'scheduled'")
            query.setInt(1,idClient.toInt())
            query.setInt(2,idProperty.toInt())

            val result = query.executeQuery()

            if (result.next()) VisitResult.FoundVisit(Visit.fromResultSet(result)) else VisitResult.NotFound()
        }
        catch (error: SQLException) {
            VisitResult.DBError(error.message.toString())
        }
    }

    fun getVisit (visitId: UInt): VisitResult {
        if (visitId == 0U) return VisitResult.Failure()

        return try {
            val query = dbConnection.prepareStatement("SELECT * FROM visit WHERE id = ?")
            query.setInt(1,visitId.toInt())

            val result = query.executeQuery()

            if (result.next()) VisitResult.FoundVisit(Visit.fromResultSet(result)) else VisitResult.NotFound()
        }
        catch (error: SQLException) {
            VisitResult.DBError(error.message.toString())
        }
    }

    fun getAllByClient (idClient: UInt): VisitResult {
        if (idClient == 0U) return VisitResult.Failure()

        val visitsList = ArrayList<Visit>()
        return try {
            val query = dbConnection.prepareStatement("SELECT * FROM visit WHERE clientId = ?")
            query.setInt(1,idClient.toInt())

            val result = query.executeQuery()

            while (result.next()) visitsList.add(Visit.fromResultSet(result))

            VisitResult.FoundList(visitsList)
        }
        catch (error: SQLException) {
            VisitResult.DBError(error.message.toString())
        }
    }
}
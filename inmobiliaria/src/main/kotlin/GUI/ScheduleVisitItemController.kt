package GUI

import DAO.VisitDAO
import DAO.VisitResult
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Label
import DTO.Visit
import DTO.VisitStatus
import java.sql.Date
import java.sql.Time

class ScheduleVisitItemController {
    @FXML lateinit var lbTime : Label
    private var propertyId: UInt = 0U
    private var clientId : UInt = 0U
    private lateinit var date : Date
    private lateinit var time: Time
    private lateinit var parentController: ScheduleVisitController

    fun initialize (propertyId: UInt, clientId: UInt, date: Date, time: String, parentController: ScheduleVisitController) {
        this.propertyId = propertyId
        this.clientId = clientId
        this.date = date
        this.time = Time.valueOf(time)
        this.lbTime.text = time
        this.parentController = parentController
    }

    @FXML fun agendar () {
        if (hasAVisitAlready()) showAlert("Ya tiene una visita agendada en esta propiedad.\nDiríjase a la sección de Agenda",Alert.AlertType.INFORMATION)
        else if (hasAVisitAtTheSameTime()) showAlert("Ya tiene una visita agendada a esa hora y día. Elija otra fecha",Alert.AlertType.WARNING)
        else if (hasAVisitWithinTheLimit(1)) showAlert("Ya tiene una visita agendada una hora después.\nLe recomendamos calcular sus tiempos o reagendar si es necesario",Alert.AlertType.WARNING)
        else if (hasAVisitWithinTheLimit(-1)) showAlert("Ya tiene una visita agendada una hora antes.\nLe recomendamos calcular sus tiempos o reagendar si es necesario",Alert.AlertType.WARNING)
        else {
            val visitDao = VisitDAO()
            when (val visitResult = visitDao.add(Visit(clientId = clientId, propertyId = propertyId, date = date, time = time, visitStatus = VisitStatus.scheduled))) {
                is VisitResult.Success -> {
                    showAlert(visitResult.message,Alert.AlertType.INFORMATION)
                    parentController.searchSchedule()
                }
                is VisitResult.DBError -> showAlert("Error al establecer conexión con la base de datos",Alert.AlertType.ERROR)
                is VisitResult.Failure -> showAlert(visitResult.message,Alert.AlertType.ERROR)
                is VisitResult.WrongVisit -> showAlert(visitResult.message,Alert.AlertType.WARNING)
                else -> showAlert("Algo salió mal. Intentalo de nuevo más tarde",Alert.AlertType.ERROR)
            }
        }
    }

    private fun hasAVisitAlready (): Boolean {
        val visitDao = VisitDAO()
        return when (visitDao.getVisit(this.clientId,this.propertyId)) {
            is VisitResult.FoundVisit -> true
            is VisitResult.Failure -> {
                showAlert("Ocurrió un error, reinicie la aplicación",Alert.AlertType.WARNING)
                true
            }
            is VisitResult.NotFound -> false
            is VisitResult.DBError -> {
                showAlert("Error al establecer conexión con la base de datos",Alert.AlertType.ERROR)
                false
            }
            else -> false
        }
    }

    private fun hasAVisitAtTheSameTime (): Boolean {
        val visitDao = VisitDAO()
        return when (val visitResult = visitDao.getVisit(this.clientId,this.date,this.time)) {
            is VisitResult.NotFound -> false
            is VisitResult.FoundVisit -> true
            else -> {
                showAlert(visitResult.message,Alert.AlertType.ERROR)
                true
            }
        }
    }

    private fun hasAVisitWithinTheLimit (range: Long): Boolean {
        val limitTime = Time.valueOf(this.time.toLocalTime().plusHours(range))
        val visitDao = VisitDAO()
        return when (val visitResult = visitDao.getVisit(this.clientId,this.date,limitTime)) {
            is VisitResult.NotFound -> false
            is VisitResult.FoundVisit -> true
            else -> {
                showAlert(visitResult.message,Alert.AlertType.ERROR)
                true
            }
        }
    }

    private fun showAlert (message : String, type : Alert.AlertType) {
        val alert = Alert(type)
        alert.contentText = message
        alert.show()
    }

}
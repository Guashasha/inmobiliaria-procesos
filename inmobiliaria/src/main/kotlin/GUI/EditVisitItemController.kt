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
import java.time.LocalDate
import java.time.LocalTime

class EditVisitItemController {
    @FXML lateinit var lbTime : Label

    private lateinit var date: Date
    private lateinit var time: Time
    private lateinit var editVisitController : EditVisitController
    private lateinit var visit: Visit

    fun initialize (time: String, visit: Visit, date: Date, editVisitController: EditVisitController) {
        this.visit = visit
        this.date = date
        this.time = Time.valueOf(time)
        this.lbTime.text = time
        this.editVisitController = editVisitController
    }

    @FXML fun reagendar () {
        verifyValidity()
        if (hasAVisitAtTheSameTime()) showAlert("Ya tiene una visita agendada a esa hora y día. Elija otra fecha",Alert.AlertType.WARNING)
        else if (hasAVisitWithinTheLimit()) showAlert("Ya tiene una visita agendada una hora antes.\nLe recomendamos calcular sus tiempos o reagendar si es necesario",Alert.AlertType.WARNING)
        else if (this.visit.visitStatus == VisitStatus.scheduled) {
            val visitDao = VisitDAO()
            this.visit.date = this.date
            this.visit.time = this.time
            when (val visitResult = visitDao.edit(visit)) {
                is VisitResult.Success -> {
                    showAlert(visitResult.message,Alert.AlertType.INFORMATION)
                    this.editVisitController.exit()
                }
                is VisitResult.WrongVisit -> showAlert("Ah ocurrido un error. Reinicie la aplicación",Alert.AlertType.WARNING)
                is VisitResult.Failure -> showAlert("Ah ocurrido un error. Reinicie la aplicación",Alert.AlertType.ERROR)
                is VisitResult.DBError -> showAlert("Error al establecer conexión con la base de datos", Alert.AlertType.ERROR)
                else -> showAlert("Ah ocurrido un error. Contacte a un técnico",Alert.AlertType.ERROR)
            }
        }
    }

    private fun verifyValidity () {
        val actualDate = Date.valueOf(LocalDate.now())

        val expiredVisit = if (actualDate.equals(this.visit.date)) {
            val actualTime = Time.valueOf(LocalTime.now())
            actualTime.after(this.visit.time)
        } else actualDate.after(this.visit.date)

        if (expiredVisit) changeExpiredVisit()
    }

    private fun changeExpiredVisit () {
        this.visit.visitStatus = VisitStatus.expired
        val visitDao = VisitDAO()
        when (visitDao.edit(this.visit)) {
            is VisitResult.Success -> {
                showAlert("La visita ha expirado. No puedes reagendarla",Alert.AlertType.INFORMATION)
                this.editVisitController.exit()
            }
            is VisitResult.WrongVisit -> showAlert("Ah ocurrido un error. Reinicie la aplicación",Alert.AlertType.WARNING)
            is VisitResult.Failure -> showAlert("Ah ocurrido un error. Reinicie la aplicación",Alert.AlertType.ERROR)
            is VisitResult.DBError -> showAlert("Error al establecer conexión con la base de datos", Alert.AlertType.ERROR)
            else -> showAlert("Ah ocurrido un error. Contacte a un técnico",Alert.AlertType.ERROR)
        }
    }

    private fun hasAVisitAtTheSameTime (): Boolean {
        val visitDao = VisitDAO()
        return when (val visitResult = visitDao.getVisit(this.visit.clientId,this.date,this.time)) {
            is VisitResult.NotFound -> false
            is VisitResult.FoundVisit -> true
            else -> {
                showAlert(visitResult.message,Alert.AlertType.ERROR)
                true
            }
        }
    }

    private fun hasAVisitWithinTheLimit (): Boolean {
        val limitTime = Time.valueOf(this.time.toLocalTime().minusHours(1))
        val visitDao = VisitDAO()
        return if (limitTime == this.visit.time) false
        else when (val visitResult = visitDao.getVisit(this.visit.clientId,this.date,limitTime)) {
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
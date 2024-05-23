package GUI

import DAO.VisitDAO
import DAO.VisitResult
import javafx.application.Application
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.DatePicker
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import main.kotlin.DTO.Visit
import java.io.IOException
import java.sql.Date
import java.time.LocalDate

fun main (args: Array<String>) {
    Application.launch(ScheduleVisitController::class.java)
}

class ScheduleVisitController : Application() {
    @FXML lateinit var gpSchedule : GridPane
    @FXML lateinit var dpDate : DatePicker
    var propertyId : UInt = 1U
    var clientId : UInt = 1U
    override fun start (primaryStage: Stage) {
        try {
            val parent: Parent? = FXMLLoader.load<Parent>(javaClass.getResource("/FXML/ScheduleVisit.fxml"))

            primaryStage.run {
                title = "Agendar visita"
                scene = Scene(parent)
                show()
            }
        }
        catch (error: IOException) {
            throw RuntimeException(error)
        }
    }

    @FXML fun salir () {

    }

    @FXML fun searchSchedule () {
        val visitDao = VisitDAO()
        val selectedDate : LocalDate? = dpDate.value

        if (selectedDate != null) {
            val visitResult = visitDao.getUnavailableVisits(propertyId,Date.valueOf(selectedDate))
            when (visitResult) {
                is VisitResult.FoundList -> showSchedule(visitResult.visits)
                is VisitResult.DBError -> showAlert(visitResult.message,AlertType.ERROR)
                is VisitResult.Failure -> showAlert(visitResult.message,AlertType.ERROR)
                else -> showAlert("Algo salió mal. Contacte a un técnico",AlertType.ERROR)
            }
        }
        else showAlert("Debe seleccionar una fecha",AlertType.WARNING)
    }

    private fun showSchedule (unavailableVisits : List<Visit>) {
        val availableSchedule = mutableListOf("08:00:00","09:00:00","10:00:00","11:00:00","12:00:00","13:00:00","14:00:00","15:00:00","16:00:00")

        for (visit in unavailableVisits) {
            val time = visit.time.toString()
            availableSchedule.remove(time)
        }

        var column = 0
        var row = 0
        gpSchedule.children.clear()

        for (time in availableSchedule) {
            val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/ScheduleVisitItem.fxml"))

            try {
                val panetime: BorderPane = fxmlLoader.load()
                val scheduleVisitItemController = fxmlLoader.getController<ScheduleVisitItemController>()
                scheduleVisitItemController.lbTime.text = time
                scheduleVisitItemController.propertyId = propertyId
                scheduleVisitItemController.clientId = clientId
                scheduleVisitItemController.date = Date.valueOf(dpDate.value)
                scheduleVisitItemController.parentController = this
                gpSchedule.add(panetime,column,row)
            }
            catch (error: IOException) {
                showAlert("Archivos corrompidos. Intentelo de nuevo más tarde",AlertType.ERROR)
                break
            }

            if (column == 2) {
                row++
                column = 0
            } else column++
        }
    }

    private fun showAlert (message : String, type : AlertType) {
        val alert = Alert(type)
        alert.contentText = message
        alert.show()
    }
}
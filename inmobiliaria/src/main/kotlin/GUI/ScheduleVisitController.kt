package GUI

import DAO.VisitDAO
import DAO.VisitResult
import javafx.application.Application
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
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
    var idProperty : UInt = 1U
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

    @FXML fun buscarHorarios () {
        val visitDao = VisitDAO()
        val selectedDate : LocalDate? = dpDate.value

        if (selectedDate != null) {
            val visitResult = visitDao.getUnavailableVisits(idProperty,Date.valueOf(selectedDate))
            when (visitResult) {
                is VisitResult.FoundList -> showSchedule(visitResult.visits)
                is VisitResult.DBError -> showAlert("Error en la conexión con la base de datos")
                else -> showAlert("Algo salió mal. Contacte a un técnico")
            }
        }
        else
            showAlert("Debe seleccionar una fecha")
    }

    private fun showSchedule (unavailableVisits : List<Visit>) {
        val availableSchedule = mutableListOf("08:00:00","09:00:00","10:00:00","11:00:00","12:00:00","13:00:00","14:00:00","15:00:00","16:00:00")

        for (visit in unavailableVisits) {
            val date = visit.date.toString()
            availableSchedule.remove(date)
        }

        var column = 0
        var row = 0

        for (time in availableSchedule) {
            val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/ScheduleVisitItem.fxml"))

            try {
                val panetime: BorderPane = fxmlLoader.load()
                gpSchedule.add(panetime,column,row)
            }
            catch (error: IOException) {
                showAlert("Archivos corrompidos. Intentelo de nuevo más tarde")
                break
            }

            if (row == 2) {
                column++
                row = 0
            } else row++
        }
    }

    private fun showAlert (message : String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.contentText = message
        alert.show()
    }



}
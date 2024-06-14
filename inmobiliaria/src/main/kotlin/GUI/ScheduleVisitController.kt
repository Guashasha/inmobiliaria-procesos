package GUI

import DAO.VisitDAO
import DAO.VisitResult
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import DTO.Visit
import java.io.IOException
import java.sql.Date
import java.time.LocalDate

class ScheduleVisitController {
    private lateinit var bpMain: BorderPane
    private lateinit var lastPane: Pane
    private lateinit var lbHeader: Label
    private var propertyId : UInt = 0U
    private var clientId : UInt = 0U
    @FXML lateinit var gpSchedule : GridPane
    @FXML lateinit var dpDate : DatePicker

    fun initialize (bpMain: BorderPane, lastPane: Pane, lbHeader: Label, propertyId: UInt, clientId: UInt) {
        this.bpMain = bpMain
        this.lastPane = lastPane
        this.lbHeader = lbHeader
        this.propertyId = propertyId
        this.clientId = clientId
        limitDateOnDatePicker()
    }

    private fun limitDateOnDatePicker () {
        this.dpDate.dayCellFactory = javafx.util.Callback {
            object : javafx.scene.control.DateCell() {
                override fun updateItem(item: LocalDate?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (item?.isBefore(LocalDate.now().plusDays(1)) == true) {
                        isDisable = true
                        style = "-fx-background-color: #ffc0cb;"
                    }
                }
            }
        }
    }

    @FXML fun salir () {
        this.lbHeader.text = "Información de propiedad"
        this.bpMain.center = lastPane
    }

    @FXML fun searchSchedule () {
        val visitDao = VisitDAO()
        val selectedDate : LocalDate? = dpDate.value

        if (selectedDate != null) {
            when (val visitResult = visitDao.getUnavailableVisits(propertyId,Date.valueOf(selectedDate))) {
                is VisitResult.FoundList -> showSchedule(visitResult.visits,Date.valueOf(selectedDate))
                is VisitResult.DBError -> showAlert("Error al establecer conexión con la base de datos",AlertType.ERROR)
                is VisitResult.Failure -> showAlert(visitResult.message,AlertType.ERROR)
                else -> showAlert("Algo salió mal. Contacte a un técnico",AlertType.ERROR)
            }
        }
        else showAlert("Debe seleccionar una fecha",AlertType.WARNING)
    }

    private fun showSchedule (unavailableVisits : List<Visit>, date: Date) {
        val availableSchedule = mutableListOf("08:00:00","09:00:00","10:00:00","11:00:00","12:00:00","13:00:00","14:00:00","15:00:00","16:00:00")

        for (visit in unavailableVisits) {
            val time = visit.time.toString()
            availableSchedule.remove(time)
        }

        var column = 0
        var row = 0
        gpSchedule.children.clear()

        if (availableSchedule.isEmpty()) {
            showAlert("Ya no quedan más horarios disponibles para esta fecha",AlertType.INFORMATION)
        }

        for (time in availableSchedule) {
            val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/ScheduleVisitItem.fxml"))

            try {
                val paneTime: BorderPane = fxmlLoader.load()
                val scheduleVisitItemController = fxmlLoader.getController<ScheduleVisitItemController>()
                scheduleVisitItemController.initialize(this.propertyId,this.clientId,date,time,this)
                gpSchedule.add(paneTime,column,row)
            }
            catch (error: IOException) {
                showAlert("Archivos corrompidos. Inténtelo de nuevo más tarde",AlertType.ERROR)
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
        alert.showAndWait()
    }
}
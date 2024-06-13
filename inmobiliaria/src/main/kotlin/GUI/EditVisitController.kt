package GUI

import DAO.VisitDAO
import DAO.VisitResult
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import main.kotlin.DAO.PropertyDAO
import main.kotlin.DAO.PropertyResult
import DTO.Visit
import DTO.VisitStatus
import java.io.IOException
import java.sql.Date
import java.sql.Time
import java.time.LocalDate
import java.time.LocalTime

class EditVisitController {
    @FXML lateinit var lbVisitDate: Label
    @FXML lateinit var lbVisitTime: Label
    @FXML lateinit var lbTitle: Label
    @FXML lateinit var lbShortDescription: Label
    @FXML lateinit var lbAddress: Label
    @FXML lateinit var dpDate: DatePicker
    @FXML lateinit var gpSchedule: GridPane

    private lateinit var bpMain: BorderPane
    private lateinit var lastPane: Pane
    private lateinit var lbHeader: Label
    private lateinit var visitScheduleController: VisitScheduleController
    private lateinit var visit: Visit

    fun initialize (visit: Visit, bpMain: BorderPane, lastPane: Pane, lbHeader: Label, visitScheduleController: VisitScheduleController) {
        this.visit = visit
        this.bpMain = bpMain
        this.lastPane = lastPane
        this.lbHeader = lbHeader
        this.lbHeader.text = "Configuraciones de visita"
        this.visitScheduleController = visitScheduleController
        limitDateOnDatePicker()
        loadPropertyInformation()
        loadVisitInformation()
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

    @FXML fun cancelVisit () {
        verifyValidity()
        if (this.visit.visitStatus == VisitStatus.scheduled) {
            val alert = Alert(Alert.AlertType.CONFIRMATION)
            alert.title = "Cancelar visita"
            alert.contentText = "¿Está seguro que desea cancelar la visita?"
            val result = alert.showAndWait()
            if (result.isPresent && result.get() == ButtonType.OK) cancelVisitConfirmation()
        }
    }

    @FXML fun searchSchedule () {
        val selectedDate : LocalDate? = dpDate.value

        if (selectedDate != null) {
            val visitDao = VisitDAO()
            when (val visitResult = visitDao.getUnavailableVisits(this.visit.propertyId,Date.valueOf(selectedDate))) {
                is VisitResult.FoundList -> showSchedule(visitResult.visits)
                is VisitResult.DBError -> showAlert("Error al establecer conexión con la base de datos", Alert.AlertType.ERROR)
                is VisitResult.Failure -> showAlert(visitResult.message, Alert.AlertType.ERROR)
                else -> showAlert("Algo salió mal. Contacte a un técnico", Alert.AlertType.ERROR)
            }
        }
        else showAlert("Debe seleccionar una fecha", Alert.AlertType.WARNING)
    }

    @FXML fun exit () {
        this.lbHeader.text = "Agenda"
        this.bpMain.center = lastPane
        this.visitScheduleController.loadScheduleVisits()
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
                showAlert("La visita ha expirado. No puedes cancelarla",Alert.AlertType.INFORMATION)
                exit()
            }
            is VisitResult.WrongVisit -> showAlert("Ah ocurrido un error. Reinicie la aplicación",Alert.AlertType.WARNING)
            is VisitResult.Failure -> showAlert("Ah ocurrido un error. Reinicie la aplicación",Alert.AlertType.ERROR)
            is VisitResult.DBError -> showAlert("Error al establecer conexión con la base de datos", Alert.AlertType.ERROR)
            else -> showAlert("Ah ocurrido un error. Contacte a un técnico",Alert.AlertType.ERROR)
        }
    }

    private fun cancelVisitConfirmation () {
        this.visit.visitStatus = VisitStatus.cancelled
        val visitDao = VisitDAO()
        when (val visitResult = visitDao.edit(this.visit)) {
            is VisitResult.Success -> {
                showAlert(visitResult.message,Alert.AlertType.INFORMATION)
                exit()
            }
            is VisitResult.Failure -> showAlert("Algo salió mal. Reinicie la aplicación",Alert.AlertType.WARNING)
            is VisitResult.WrongVisit -> showAlert("Algo salió mal al intentar cancelar la visita", Alert.AlertType.ERROR)
            is VisitResult.DBError -> showAlert("Error al establecer conexión con la base de datos",Alert.AlertType.ERROR)
            else -> showAlert("Algo salió mal. Contacte con un técnico",Alert.AlertType.ERROR)
        }
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

        if (availableSchedule.isEmpty()) {
            showAlert("Ya no quedan más horarios disponibles para esta fecha", Alert.AlertType.INFORMATION)
        }

        for (time in availableSchedule) {
            val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/EditVisitItem.fxml"))

            try {
                val paneTime: BorderPane = fxmlLoader.load()
                val editVisitItemController = fxmlLoader.getController<EditVisitItemController>()
                editVisitItemController.initialize(time,this.visit,Date.valueOf(dpDate.value),this)
                gpSchedule.add(paneTime,column,row)
            }
            catch (error: IOException) {
                showAlert("Archivos corrompidos. Inténtelo de nuevo más tarde", Alert.AlertType.ERROR)
                break
            }

            if (column == 2) {
                row++
                column = 0
            } else column++
        }
    }

    private fun loadVisitInformation () {
        val visitDAO = VisitDAO()
        when (val visitResult = visitDAO.getVisit(this.visit.id)) {
            is VisitResult.FoundVisit -> {
                this.lbVisitDate.text = visitResult.visit.date.toString()
                this.lbVisitTime.text = visitResult.visit.time.toString()
            }
            is VisitResult.NotFound -> showAlert(visitResult.message,Alert.AlertType.ERROR)
            is VisitResult.Failure -> showAlert("Algo salió mal. Reinicie la aplicación",Alert.AlertType.WARNING)
            is VisitResult.DBError -> showAlert(visitResult.message,Alert.AlertType.ERROR)
            else -> showAlert("Algo salió mal. Contacte con un técnico",Alert.AlertType.ERROR)
        }
    }

    private fun loadPropertyInformation () {
        val propertyDao = PropertyDAO()
        when (val propertyResult = propertyDao.getById(visit.propertyId)) {
            is PropertyResult.Found -> {
                this.lbTitle.text = propertyResult.property.title
                this.lbShortDescription.text = propertyResult.property.shortDescription
                this.lbAddress.text = propertyResult.property.direction
            }
            is PropertyResult.WrongProperty -> showAlert("Algo salió mal. Reinicie la aplicación", Alert.AlertType.WARNING)
            is PropertyResult.DBError -> showAlert(propertyResult.message, Alert.AlertType.ERROR)
            else -> showAlert("Algo salió mal. Contacte con un técnico",Alert.AlertType.ERROR)
        }
    }

    private fun showAlert (message : String, type : Alert.AlertType) {
        val alert = Alert(type)
        alert.contentText = message
        alert.showAndWait()
    }

}
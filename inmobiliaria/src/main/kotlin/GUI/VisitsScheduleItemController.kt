package GUI

import DAO.VisitDAO
import DAO.VisitResult
import DTO.Property
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import DTO.Visit
import DTO.VisitStatus
import java.io.IOException
import java.time.LocalDate
import java.sql.Date
import java.sql.Time
import java.time.LocalTime

class VisitsScheduleItemController {
    @FXML lateinit var lbTitle: Label
    @FXML lateinit var lbShortDescription: Label
    @FXML lateinit var lbVisitDate: Label
    @FXML lateinit var lbVisitTime: Label
    @FXML lateinit var btnConfigurar: Button
    @FXML lateinit var lbStatus: Label

    private lateinit var bpMain: BorderPane
    private lateinit var lastPane: Pane
    private lateinit var lbHeader: Label
    private lateinit var visitScheduleController: VisitScheduleController
    private lateinit var visit: Visit

    fun initialize (property : Property, visit: Visit, bpMain: BorderPane, lastPane: Pane, lbHeader: Label, visitScheduleController: VisitScheduleController) {
        this.visit = visit
        this.bpMain = bpMain
        this.lastPane = lastPane
        this.lbHeader = lbHeader
        this.visitScheduleController = visitScheduleController
        loadInformation(visit,property)
        verifyValidity()
    }

    private fun loadInformation (visit: Visit, property: Property) {
        this.lbTitle.text = property.title
        this.lbShortDescription.text = property.shortDescription
        this.lbVisitDate.text = visit.date.toString()
        this.lbVisitTime.text = visit.time.toString()
    }

    private fun verifyValidity () {
        when (this.visit.visitStatus) {
            VisitStatus.cancelled -> {
                this.btnConfigurar.isVisible = false
                this.lbStatus.text = "Cancelada"
                this.lbStatus.isVisible = true
            }
            VisitStatus.expired -> {
                this.btnConfigurar.isVisible = false
                this.lbStatus.text = "Expirada"
                this.lbStatus.isVisible = true
            }
            else -> {
                val actualDate = Date.valueOf(LocalDate.now())
                val expiredVisit = if (actualDate.equals(this.visit.date)) {
                    val actualTime = Time.valueOf(LocalTime.now())
                    actualTime.after(this.visit.time)
                } else actualDate.after(this.visit.date)

                if (expiredVisit) changeExpiredVisit()
            }
        }
    }

    private fun changeExpiredVisit () {
        this.visit.visitStatus = VisitStatus.expired
        val visitDao = VisitDAO()
        when (visitDao.edit(this.visit)) {
            is VisitResult.Success -> {
                this.btnConfigurar.isVisible = false
                this.lbStatus.text = "Expirada"
                this.lbStatus.isVisible = true
            }
            is VisitResult.WrongVisit -> showAlert("Ah ocurrido un error. Reinicie la aplicación",Alert.AlertType.WARNING)
            is VisitResult.Failure -> showAlert("Ah ocurrido un error. Reinicie la aplicación",Alert.AlertType.ERROR)
            is VisitResult.DBError -> showAlert("Error al establecer conexión con la base de datos", Alert.AlertType.ERROR)
            else -> showAlert("Ah ocurrido un error. Contacte a un técnico",Alert.AlertType.ERROR)
        }
    }

    @FXML fun openVisitSettings () {
        verifyValidity()
        if (this.visit.visitStatus == VisitStatus.scheduled) {
            val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/EditVisit.fxml"))

            try {
                val editVisitPane: BorderPane = fxmlLoader.load()
                val editVisitController = fxmlLoader.getController<EditVisitController>()
                editVisitController.initialize(this.visit,this.bpMain,this.lastPane,this.lbHeader,this.visitScheduleController)
                this.bpMain.center = editVisitPane
            } catch (error: IOException) {
                showAlert("Archivos corrompidos. Inténtelo de nuevo más tarde", Alert.AlertType.ERROR)
            }
        } else showAlert("La visita ha expirado",Alert.AlertType.INFORMATION)
    }

    private fun showAlert (message : String, type : Alert.AlertType) {
        val alert = Alert(type)
        alert.contentText = message
        alert.showAndWait()
    }

}
package GUI

import DAO.VisitDAO
import DAO.VisitResult
import DTO.Account
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import main.kotlin.DAO.PropertyDAO
import main.kotlin.DAO.PropertyResult
import DTO.Visit
import java.io.IOException

class VisitScheduleController {
    @FXML lateinit var vboxVisitsSchedule: VBox
    @FXML lateinit var bpActual: BorderPane

    private lateinit var bpMain: BorderPane
    private lateinit var lastPane: Pane
    private lateinit var lbHeader: Label
    private var clientId : UInt = 0U

    @FXML fun exit () {
        this.lbHeader.text = "Menu principal"
        this.bpMain.center = lastPane
    }

    fun initialize (bpMain: BorderPane, lastPane: Pane, account: Account, lbHeader: Label) {
        this.bpMain = bpMain
        this.lastPane = lastPane
        this.lbHeader = lbHeader
        this.clientId = account.id!!
        loadScheduleVisits()
    }

    fun loadScheduleVisits () {
        val visitDao = VisitDAO()
        when (val visitResult = visitDao.getAllByClient(this.clientId)) {
            is VisitResult.FoundList -> showScheduleVisits(visitResult.visits)
            is VisitResult.Failure -> showAlert("Algo salió mal, reinicie la aplicación",Alert.AlertType.ERROR)
            is VisitResult.DBError -> showAlert(visitResult.message,Alert.AlertType.ERROR)
            else -> showAlert("Algo salió mal. Contacte a un técnico", Alert.AlertType.ERROR)
        }
    }

    private fun showScheduleVisits (visitsList: List<Visit>) {
        vboxVisitsSchedule.children.clear()
        var unavailableProperties = 0


        for (visit in visitsList) {
            val propertyDao = PropertyDAO()

            when (val propertyResult = propertyDao.getById(visit.propertyId)) {
                is PropertyResult.Found -> {
                    val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/VisitsScheduleItem.fxml"))

                    try {
                        val visitPane: BorderPane = fxmlLoader.load()
                        val visitsScheduleItemController = fxmlLoader.getController<VisitsScheduleItemController>()
                        visitsScheduleItemController.initialize(propertyResult.property,visit,this.bpMain,this.bpActual,this.lbHeader,this)
                        vboxVisitsSchedule.children.add(visitPane)
                    } catch (error: IOException) {
                        showAlert("Archivos corrompidos. Inténtelo de nuevo más tarde", Alert.AlertType.ERROR)
                        break
                    }
                }
                is PropertyResult.WrongProperty -> showAlert("Algo salió mal. Reinicie la aplicación",Alert.AlertType.ERROR)
                is PropertyResult.DBError -> {
                    showAlert(propertyResult.message,Alert.AlertType.ERROR)
                    break
                }
                else -> unavailableProperties++
            }
        }

        if (unavailableProperties > 0) showAlert("$unavailableProperties propiedades fueron archivadas o borradas",Alert.AlertType.WARNING)
    }

    private fun showAlert (message : String, type : Alert.AlertType) {
        val alert = Alert(type)
        alert.contentText = message
        alert.showAndWait()
    }
}
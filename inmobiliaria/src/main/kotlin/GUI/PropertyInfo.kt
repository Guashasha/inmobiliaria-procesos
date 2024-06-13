package GUI;

import DTO.Account
import DTO.AccountType
import DTO.Property
import DTO.PropertyAction
import GUI.Utility.PopUpAlert
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.stage.Stage
import main.kotlin.DAO.PropertyDAO
import main.kotlin.DAO.PropertyResult
import java.io.IOException

class PropertyInfo {
    private lateinit var mainPane: BorderPane
    private lateinit var originalPane: Pane
    private lateinit var account: Account
    private lateinit var lbHeader: Label

    @FXML
    private lateinit var imgPrincipal: ImageView
    @FXML
    private lateinit var lbTitle: Text
    @FXML
    private lateinit var lbFullDescription: Text
    @FXML
    private lateinit var lbPropertyAction: Text
    @FXML
    private lateinit var lbPrice: Text
    @FXML
    private lateinit var apRoot: AnchorPane

    private lateinit var property: Property

    fun initialize(mainPane: BorderPane, originalPane: Pane, account: Account, lbHeader : Label, property: Property) {
        this.mainPane = mainPane
        this.originalPane = originalPane
        this.account= account
        this.lbHeader = lbHeader
        this.property = property

        setPropertyData()
    }

    fun scheduleVisit () {
        if (account.type == AccountType.AGENT) {
            PopUpAlert.showAlert("No puede agendar visitas siendo administrador",  Alert.AlertType.ERROR)
            return
        }

        val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/ScheduleVisit.fxml"))
        var bpScheduleVisit : BorderPane? = null

        try {
            bpScheduleVisit = fxmlLoader.load()
        }
        catch (error : IOException) {
            PopUpAlert.showAlert("Error al cargar la ventana de información", Alert.AlertType.WARNING)
        }

        if (bpScheduleVisit != null && property.id != null && account.id != null) {
            val scheduleVisitController = fxmlLoader.getController<ScheduleVisitController>()
            scheduleVisitController.initialize(mainPane,apRoot,lbHeader, property.id!!, account.id!!)
            this.lbHeader.text = "Agenda"
            mainPane.center = bpScheduleVisit
            val stage = mainPane.scene.window as Stage
            stage.title = "Agendar visita a la propiedad"
        }
    }

    fun returnToList () {
        this.lbHeader.text = "Lista de propiedades"
        this.mainPane.center = originalPane
    }

    private fun getPropertyData () {
        val dao = PropertyDAO()

        if (property.id == null) {
            return
        }

        val result = dao.getImage(property.id!!)

        property.image = when (result) {
            is PropertyResult.DBError -> {
                PopUpAlert.showAlert("No se pudo conectar con la base de datos, intente de nuevo más tarde: " + result.message, Alert.AlertType.ERROR)
                null
            }
            is PropertyResult.FoundImage -> {
                result.image
            }
            is PropertyResult.NotFound -> {
                null
            }
            is PropertyResult.WrongProperty -> {
                PopUpAlert.showAlert(result.message, Alert.AlertType.WARNING)
                null
            }
            else -> {
                PopUpAlert.showAlert("Ocurrió un error inesperado al recuperar la información de la propiedad.", Alert.AlertType.ERROR)
                null
            }
        }
    }

    private fun setPropertyData () {
        getPropertyData()

        imgPrincipal.image = property.image

        lbTitle.text = property.title
        lbFullDescription.text = property.fullDescription
        lbPrice.text = property.price.toString()

        lbPropertyAction.text = when (property.action) {
            PropertyAction.sell -> "venta"
            PropertyAction.rent -> "renta"
        }.toString()
    }
}

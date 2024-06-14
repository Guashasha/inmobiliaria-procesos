package GUI

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

class AdminPropertyInfo {
    private lateinit var mainPane: BorderPane
    private lateinit var originalPane: Pane
    private lateinit var account: Account
    private lateinit var lbHeader: Label

    @FXML
    private lateinit var hboxImages: HBox
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
    @FXML
    private lateinit var lbCity: Text
    @FXML
    private lateinit var lbNumRooms: Text
    @FXML
    private lateinit var lbNumBathrooms: Text
    @FXML
    private lateinit var lbGarage: Text
    @FXML
    private lateinit var lbGarden: Text
    @FXML
    private lateinit var lbSize: Text

    private lateinit var property: Property

    fun initialize(mainPane: BorderPane, originalPane: Pane, account: Account, lbHeader : Label, property: Property) {
        this.mainPane = mainPane
        this.originalPane = originalPane
        this.account= account
        this.lbHeader = lbHeader
        this.property = property

        setPropertyData()
    }

    fun openModifyProperty () {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/ModifyProperty.fxml"))
        var paneModifyProperty : Pane? = null

        try {
            paneModifyProperty = fxmlLoader.load()
        }
        catch (error : IOException) {
            PopUpAlert.showAlert("Error al cargar la ventana de información", Alert.AlertType.WARNING)
        }

        if (paneModifyProperty != null && property.id != null && account.id != null) {
            val scheduleVisitController = fxmlLoader.getController<ModifyProperty>()
            scheduleVisitController.initialize(mainPane, apRoot, lbHeader, property)
            this.lbHeader.text = "Modificar datos de propiedad"
            mainPane.center = paneModifyProperty
            val stage = mainPane.scene.window as Stage
            stage.title = "Modificar datos de propiedad"
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
                PopUpAlert.showAlert("No se pudo conectar con la base de datos, intente de nuevo más tarde", Alert.AlertType.ERROR)
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
        lbCity.text = property.city
        lbSize.text = "Tamaño de propiedad: " + property.size.toString() + "m2"
        lbNumRooms.text = "Numero de cuartos: " + property.numRooms.toString()
        lbNumBathrooms.text = "Numero de baños: " + property.numBathrooms.toString()

        lbGarden.isVisible = property.garden
        lbGarage.isVisible = property.garage

        lbPropertyAction.text = when (property.action) {
            PropertyAction.sell -> "venta"
            PropertyAction.rent -> "renta"
        }.toString()
    }
}
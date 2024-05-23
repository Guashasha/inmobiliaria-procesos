package GUI;

import DTO.Account
import DTO.Property
import DTO.PropertyAction
import GUI.Utility.PopUpAlert
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import main.kotlin.DAO.PropertyDAO
import main.kotlin.DAO.PropertyResult

class PropertyInfo {
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

    private var propertyId: UInt? = 1u
        set (value) {
            if (value != null) {
                field = if (value < 1u) {
                    null
                } else {
                    value
                }
            }
        }

    private var property: Property? = null

    fun initialize(mainPane: BorderPane, originalPane: Pane, account: Account, lbHeader : Label) {
        this.mainPane = mainPane
        this.originalPane = originalPane
        this.account= account
        this.lbHeader = lbHeader
        setPropertyData()
    }

    fun modifyProperty () {
        print("crear ventana de modificación")
    }

    private fun getPropertyData () {
        val dao = PropertyDAO()

        if (propertyId == null) {
            PopUpAlert.showAlert("No se puede realizar la operación, intente recargar la aplicación.", Alert.AlertType.ERROR)
            return
        }

        var result = dao.getById(propertyId!!)

        when (result) {
            is PropertyResult.DBError -> PopUpAlert.showAlert(result.message, Alert.AlertType.ERROR)
            is PropertyResult.Found -> this.property = result.property
            is PropertyResult.NotFound -> PopUpAlert.showAlert(result.message, Alert.AlertType.INFORMATION)
            is PropertyResult.WrongProperty -> PopUpAlert.showAlert(result.message, Alert.AlertType.WARNING)
            else -> PopUpAlert.showAlert("Ocurrió un error inesperado al recuperar la información de la propiedad.", Alert.AlertType.ERROR)
        }

        result = dao.getImages(propertyId!!)

        property?.images = when (result) {
            is PropertyResult.DBError -> {
                PopUpAlert.showAlert(result.message, Alert.AlertType.ERROR)
                null
            }
            is PropertyResult.FoundList<*> -> {
                val images = ArrayList<Image>()
                result.list.forEach { images.add(it as Image) }
                images
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

        if (property == null) {
            return
        }

        imgPrincipal.image = property?.images?.first()

        lbTitle.text = property?.title ?: "No titulo"
        lbFullDescription.text = property?.fullDescription ?: "No descripción"
        lbPrice.text = property?.price.toString()

        lbPropertyAction.text = when (property!!.action) {
            PropertyAction.sell -> "venta"
            PropertyAction.rent -> "renta"
        }.toString()
    }
}

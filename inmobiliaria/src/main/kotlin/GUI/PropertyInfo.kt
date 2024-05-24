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

    private lateinit var property: Property

    fun initialize(mainPane: BorderPane, originalPane: Pane, account: Account, lbHeader : Label, property: Property) {
        this.mainPane = mainPane
        this.originalPane = originalPane
        this.account= account
        this.lbHeader = lbHeader
        this.property = property

        setPropertyData()
    }

    fun modifyProperty () {
        print("crear ventana de modificación")
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

        val result = dao.getImages(property.id!!)

        property.images = when (result) {
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

        imgPrincipal.image = property.images?.first()

        lbTitle.text = property.title
        lbFullDescription.text = property.fullDescription
        lbPrice.text = property.price.toString()

        lbPropertyAction.text = when (property.action) {
            PropertyAction.sell -> "venta"
            PropertyAction.rent -> "renta"
        }.toString()
    }
}

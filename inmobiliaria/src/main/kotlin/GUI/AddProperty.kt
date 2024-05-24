package GUI

import DTO.*
import GUI.Utility.PopUpAlert
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import javafx.stage.Stage
import main.kotlin.DAO.PropertyDAO
import main.kotlin.DAO.PropertyResult
import java.io.File

class AddProperty {
    private lateinit var lastWindow: Pane
    private lateinit var lbHeader: Label
    private lateinit var bpMain: BorderPane

    var propertyImages = ArrayList<File>()

    @FXML
    private lateinit var cbPropertyAction: ComboBox<PropertyAction>
    @FXML
    private lateinit var cbPropertyType: ChoiceBox<PropertyType>
    @FXML
    private lateinit var tfTitle: TextField
    @FXML
    private lateinit var tfShortDescription: TextField
    @FXML
    private lateinit var tfFullDescription: TextArea
    @FXML
    private lateinit var tfDirection: TextField
    @FXML
    private lateinit var tfOwnerEmail: TextField
    @FXML
    private lateinit var tfPrice: TextField
    @FXML
    private lateinit var pnImages: GridPane

    fun initialize (lastWindow: Pane, bpMain: BorderPane, lbHeader: Label) {
        this.bpMain = bpMain
        this.lastWindow = lastWindow
        this.lbHeader = lbHeader

        cbPropertyType.items.addAll(PropertyType.house, PropertyType.building, PropertyType.premises, PropertyType.apartment)
        cbPropertyAction.items.addAll(PropertyAction.sell, PropertyAction.rent)
    }

    fun returnToMainMenu () {
        this.lbHeader.text = "Menu principal"
        this.bpMain.center = lastWindow
    }

    fun addImage () {
        val chooser = FileChooser()
        chooser.title = "Selecciona una imagen"
        chooser.extensionFilters.addAll(FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp"))

        val imageFile = chooser.showOpenDialog(Stage())

        if (imageFile != null) {
            val imagePath = imageFile.toURI().toURL().toString()
            val image = Image(imagePath)

            propertyImages.add(imageFile)

            if (!addImageToPane(image)) {
                PopUpAlert.showAlert("No se pudo agregar la imagen", Alert.AlertType.WARNING)
            }
        }
        else {
            PopUpAlert.showAlert("Ocurrió un problema al cargar la imagen, intente de nuevo.", Alert.AlertType.ERROR)
        }
    }

    fun addImageToPane (image: Image): Boolean {
        for (i in 0..5) {
            if (pnImages.children[i] == null) {
                pnImages.children.add(i, ImageView(image))
                return true
            }
        }

        return false
    }

    fun removeImages () {
        pnImages.children.removeAll()
    }

    fun createProperty (): Property? {
        if (emptyTextFields()) {
            PopUpAlert.showAlert("Por favor llene todos los campos.", Alert.AlertType.WARNING)
            return null
        }
        else if (wrongFieldsValues()) {
            return null
        }

        val title = tfTitle.text
        val shortDescription = tfShortDescription.text
        val fullDescription = tfFullDescription.text
        val price = tfPrice.text.toFloat()
        val direction = tfDirection.text
        val propertyType = cbPropertyType.value
        val propertyAction = cbPropertyAction.value
        val houseOwner = getOwner(tfOwnerEmail.text) ?: return null

        return Property(null, title, shortDescription, fullDescription, propertyType, price, PropertyState.available, direction, houseOwner.id!!, propertyAction, null)
    }

    fun registerProperty () {
        val property = createProperty() ?: return
        val dao = PropertyDAO()

        val result = dao.add(property)

        when (result) {
            is PropertyResult.DBError -> {
                PopUpAlert.showAlert("No se pudo conectar a la base de datos, intente de nuevo más tarde", Alert.AlertType.ERROR)
                return
            }
            is PropertyResult.Failure -> {
                PopUpAlert.showAlert("No se pudo agregar la propiedad, intente de nuevo.", Alert.AlertType.ERROR)
                return
            }
            is PropertyResult.Success -> {
                PopUpAlert.showAlert("Propiedad agregada exitosamente.", Alert.AlertType.INFORMATION)
            }
            is PropertyResult.WrongProperty -> {
                PopUpAlert.showAlert("Los datos ingresados para la propiedad son incorrectos, verifiquelos e intente de nuevo", Alert.AlertType.WARNING)
                returnToMainMenu()
                return
            }
            else -> {
                PopUpAlert.showAlert("Ocurrió un error inesperado", Alert.AlertType.ERROR)
                return
            }
        }

        for (image in propertyImages) {
            val result = dao.addImage(property.id!!, image)

            when (result) {
                is PropertyResult.DBError -> {
                    PopUpAlert.showAlert("No se pudo conectar a la base de datos, intente de nuevo más tarde", Alert.AlertType.ERROR)
                    return
                }
                is PropertyResult.Failure -> {
                    PopUpAlert.showAlert("No se pudo agregar la propiedad, intente de nuevo.", Alert.AlertType.ERROR)
                    return
                }
                is PropertyResult.Success -> {
                    return

                }
                else -> {
                    PopUpAlert.showAlert("Ocurrió un error inesperado", Alert.AlertType.ERROR)
                    return
                }
            }
        }
    }

    private fun getOwner (email: String): HouseOwner? {
        val dao = PropertyDAO()

        val result = dao.getOwnerByEmail(email)

        when (result) {
            is PropertyResult.DBError -> {
                PopUpAlert.showAlert("Ocurrió un error inesperado, intente de nuevo más tarde", Alert.AlertType.ERROR)
                return null
            }
            is PropertyResult.NotFound -> {
                PopUpAlert.showAlert("No se encontró el propietario con ese correo electronico", Alert.AlertType.INFORMATION)
                return null
            }
            is PropertyResult.OwnerFound -> return result.houseOwner
            else -> return null
        }
    }

    private fun emptyTextFields (): Boolean {
        return tfTitle.text.isBlank() &&
                tfShortDescription.text.isBlank() &&
                tfFullDescription.text.isBlank() &&
                tfDirection.text.isBlank() &&
                tfPrice.text.isBlank() &&
                tfOwnerEmail.text.isBlank() &&
                pnImages.children.isEmpty()
    }

    private fun wrongFieldsValues (): Boolean {
        val unsafeString = Regex("""[-*/\"'#]+""")
        val number = Regex("""[0-9]+""")
        val email = Regex("""[A-z0-9\.\-_+*]+@[A-z]+\.[A-z]+""")

        if (unsafeString.containsMatchIn(tfTitle.text)) {
            PopUpAlert.showAlert("El campo *Titulo contiene caracteres no soportados: -, *, /, \", \', #", Alert.AlertType.WARNING)
            return false
        }
        if (unsafeString.containsMatchIn(tfDirection.text)) {
            PopUpAlert.showAlert("El campo *Dirección contiene caracteres no soportados: -, *, /, \", \', #", Alert.AlertType.WARNING)
            return false
        }
        if (unsafeString.containsMatchIn(tfFullDescription.text)) {
            PopUpAlert.showAlert("El campo *Descripción completa contiene caracteres no soportados: -, *, /, \", \', #", Alert.AlertType.WARNING)
            return false
        }
        if (unsafeString.containsMatchIn(tfShortDescription.text)) {
            PopUpAlert.showAlert("El campo *Descripción corta contiene caracteres no soportados: -, *, /, \", \', #", Alert.AlertType.WARNING)
            return false
        }
        if (!email.matches(tfOwnerEmail.text)) {
            PopUpAlert.showAlert("El campo *Email no contiene una dirección de email valida, intente de nuevo", Alert.AlertType.WARNING)
            return false
        }
        if (!number.matches(tfPrice.text)) {
            PopUpAlert.showAlert("El campo *Precio no contiene un numero valido, intente de nuevo", Alert.AlertType.WARNING)
            return false
        }

        return true
    }
}
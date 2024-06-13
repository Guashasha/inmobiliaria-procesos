package GUI

import DTO.*
import GUI.Utility.PopUpAlert
import javafx.application.Application
import javafx.application.Application.launch
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
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
import java.io.IOException

fun main (args: Array<String>) {
    launch(AddProperty::class.java)
}
class AddProperty : Application() {

    private lateinit var mainWindow: BorderPane;
    private lateinit var previousWindow: Pane;
    private lateinit var headerText: Label;

    private var propertyImage: File? = null

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
    private lateinit var pnImage: ImageView

    override fun start(primaryStage: Stage) {
        try {
            val parent: Parent? = FXMLLoader.load<Parent>(javaClass.getResource("/FXML/AddProperty.fxml"))

            primaryStage.run {
                title = "Todas las propiedades"

                scene = Scene(parent)
                show()
            }
        }
        catch (error: IOException) {
            throw RuntimeException(error)
        }
    }


    fun initialize (previousWindow: Pane, mainWindow: BorderPane, headerText: Label) {
        this.previousWindow = previousWindow
        this.mainWindow = mainWindow
        this.headerText = headerText

        cbPropertyType.items.addAll(PropertyType.house, PropertyType.building, PropertyType.premises, PropertyType.apartment)
        cbPropertyType.value = PropertyType.house
        cbPropertyAction.items.addAll(PropertyAction.sell, PropertyAction.rent)
        cbPropertyAction.value = PropertyAction.sell
    }

    fun returnToMainMenu() {
        this.headerText.text = "Menu principal"
        this.mainWindow.center = previousWindow
    }

    fun addImage () {
        val chooser = FileChooser()
        chooser.title = "Selecciona una imagen"
        chooser.extensionFilters.addAll(FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp"))

        val imageFile = chooser.showOpenDialog(Stage())

        if (imageFile != null) {
            val imagePath = imageFile.toURI().toURL().toString()
            val image = Image(imagePath)

            this.propertyImage = imageFile
            pnImage.image = image
        }
    }

    fun removeImages () {
        propertyImage = null
        pnImage.image = null
    }

     fun createProperty (): Property? {
        if (emptyTextFields()) {
            PopUpAlert.showAlert("Por favor llene todos los campos.", Alert.AlertType.WARNING)
            return null
        }
        else if (wrongFieldsValues()) {
            return null
        }

        val title = tfTitle.text.trim()
        val shortDescription = tfShortDescription.text.trim()
        val fullDescription = tfFullDescription.text.trim()
        val price = tfPrice.text.toFloat()
        val direction = tfDirection.text.trim()
        val propertyType = cbPropertyType.value
        val propertyAction = cbPropertyAction.value
        val houseOwner = getOwner(tfOwnerEmail.text.trim()) ?: return null

        return Property(null, title, shortDescription, fullDescription, propertyType, price, PropertyState.available, direction, houseOwner.id!!, propertyAction, null)
    }

    fun registerProperty () {
        var property = createProperty() ?: return
        val dao = PropertyDAO()

        var result = dao.add(property)

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
                return
            }
            else -> {
                PopUpAlert.showAlert("Ocurrió un error inesperado", Alert.AlertType.ERROR)
                return
            }
        }

        result = dao.getAll()

        property = when (result) {
            is PropertyResult.DBError -> {
                PopUpAlert.showAlert("No se pudo conectar a la base de datos, intente de nuevo más tarde", Alert.AlertType.ERROR)
                return
            }
            is PropertyResult.FoundList<*> -> {
                val propertyList = result.list as ArrayList<Property>
                propertyList.first()
            }
            is PropertyResult.NotFound -> {
                PopUpAlert.showAlert("No se encontró ninguna propiedad", Alert.AlertType.INFORMATION)
                return
            }
            else -> {
                PopUpAlert.showAlert("Ocurrió un error inesperado", Alert.AlertType.ERROR)
                return
            }
        }

        if (propertyImage != null) {
            result = dao.addImage(propertyImage!!)

            when (result) {
                is PropertyResult.DBError -> {
                    PopUpAlert.showAlert("No se pudo conectar a la base de datos, intente de nuevo más tarde", Alert.AlertType.ERROR)
                    return
                }
                is PropertyResult.Failure -> {
                    PopUpAlert.showAlert("No se pudo agregar la imagen, intente de nuevo.", Alert.AlertType.ERROR)
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
            else -> {
                PopUpAlert.showAlert("Ocurrió un error inesperado", Alert.AlertType.ERROR)
                return null
            }
        }
    }

    private fun emptyTextFields (): Boolean {
        return tfTitle.text.isBlank() &&
                tfShortDescription.text.isBlank() &&
                tfFullDescription.text.isBlank() &&
                tfDirection.text.isBlank() &&
                tfPrice.text.isBlank() &&
                tfOwnerEmail.text.isBlank()
    }

    private fun wrongFieldsValues (): Boolean {
        val unsafeString = Regex("""[-*/\"'#]+""")
        val number = Regex("""[0-9]+""")
        val email = Regex("""[A-z0-9\.\-_+*]+@[A-z]+\.[A-z]+""")

        if (unsafeString.containsMatchIn(tfTitle.text)) {
            PopUpAlert.showAlert("El campo *Titulo contiene caracteres no soportados: -, *, /, \", \', #", Alert.AlertType.WARNING)
            return true
        }
        if (unsafeString.containsMatchIn(tfDirection.text)) {
            PopUpAlert.showAlert("El campo *Dirección contiene caracteres no soportados: -, *, /, \", \', #", Alert.AlertType.WARNING)
            return true
        }
        if (unsafeString.containsMatchIn(tfFullDescription.text)) {
            PopUpAlert.showAlert("El campo *Descripción completa contiene caracteres no soportados: -, *, /, \", \', #", Alert.AlertType.WARNING)
            return true
        }
        if (unsafeString.containsMatchIn(tfShortDescription.text)) {
            PopUpAlert.showAlert("El campo *Descripción corta contiene caracteres no soportados: -, *, /, \", \', #", Alert.AlertType.WARNING)
            return true
        }
        if (!email.matches(tfOwnerEmail.text)) {
            PopUpAlert.showAlert("El campo *Email no contiene una dirección de email valida, intente de nuevo", Alert.AlertType.WARNING)
            return true
        }
        if (!number.matches(tfPrice.text)) {
            PopUpAlert.showAlert("El campo *Precio no contiene un numero valido, intente de nuevo", Alert.AlertType.WARNING)
            return true
        }

        return false
    }
}
package GUI

import DTO.*
import GUI.Utility.PopUpAlert
import javafx.application.Application
import javafx.application.Application.launch
import javafx.collections.FXCollections
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
    private lateinit var tfNumRooms: TextField
    @FXML
    private lateinit var tfNumBathrooms: TextField
    @FXML
    private lateinit var tfPropertySize: TextField
    @FXML
    private lateinit var cbCity: ComboBox<String>
    @FXML
    private lateinit var ckbGarden: CheckBox
    @FXML
    private lateinit var ckbGarage: CheckBox
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

        val dao = PropertyDAO()
        val cities = dao.getCities()

        when (cities) {
            is PropertyResult.DBError -> {
                PopUpAlert.showAlert(
                    cities.message,
                    Alert.AlertType.ERROR
                )
            }
            is PropertyResult.FoundList<*> -> {
                for (city in cities.list)
                    cbCity.items.add(city.toString())
            }
            is PropertyResult.NotFound -> {
                PopUpAlert.showAlert(
                    cities.message,
                    Alert.AlertType.ERROR
                )
            }
            else -> {
                PopUpAlert.showAlert(
                    "Ocurrió un error desconocido",
                    Alert.AlertType.ERROR
                )
            }
        }
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

     private fun createProperty (): Property? {
        if (emptyTextFields()) {
            PopUpAlert.showAlert("Por favor llene todos los campos.", Alert.AlertType.WARNING)
            return null
        }
        else if (wrongFieldsValues()) {
            return null
        }
        else if (fieldValuesTooLong()) {
             return null
        }

        val title = tfTitle.text.trim()
        val shortDescription = tfShortDescription.text.trim()
        val fullDescription = tfFullDescription.text.trim()
        val price = tfPrice.text.toLong()
        val direction = tfDirection.text.trim()
        val propertyType = cbPropertyType.value
        val propertyAction = cbPropertyAction.value
        val houseOwner = getOwner(tfOwnerEmail.text.trim()) ?: return null
         val numRooms = tfNumRooms.text.trim().toInt()
         val numBathrooms = tfNumBathrooms.text.trim().toInt()
         val garage = ckbGarage.isSelected
         val garden = ckbGarden.isSelected
        val city = cbCity.value
         val size = tfPropertySize.text.trim().toLong()

        return Property(null, title, shortDescription, fullDescription, propertyType, price, PropertyState.available, direction, houseOwner.id!!, propertyAction, city, numRooms, numBathrooms, garage, garden, size, null)
    }

    private fun existingProperty (property: Property): Boolean {
        val dao = PropertyDAO()
        val otherProperties = dao.getByHouseOwner(property.houseOwner.toInt())

        when (otherProperties) {
            is PropertyResult.DBError -> {
                PopUpAlert.showAlert(otherProperties.message, Alert.AlertType.ERROR)
                return true
            }
            is PropertyResult.FoundList<*> -> {
                for (other in otherProperties.list) {
                    if (property.equals(other as Property)) {
                        PopUpAlert.showAlert("La propiedad ya existe, no puede agregar la misma propiedad más de una vez", Alert.AlertType.WARNING)
                        return true;
                    }
                }
            }
            is PropertyResult.NotFound -> return false
            else -> {
                PopUpAlert.showAlert("Ocurrió un error inesperado", Alert.AlertType.ERROR)
                return true
            }
        }

        return false;
    }

    fun registerProperty () {
        var property = createProperty() ?: return

        if (existingProperty(property)) {
            return
        }

        val dao = PropertyDAO()
        var result = dao.add(property)

        when (result) {
            is PropertyResult.DBError -> {
                PopUpAlert.showAlert("No se pudo conectar a la base de datos, intente de nuevo más tarde: " + result.message, Alert.AlertType.ERROR)
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
                PopUpAlert.showAlert("No se pudo conectar a la base de datos, intente de nuevo más tarde: " + result.message, Alert.AlertType.ERROR)
                return
            }
            is PropertyResult.FoundList<*> -> {
                val propertyList = result.list as ArrayList<Property>
                propertyList.last()
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
            result = dao.addImage(propertyImage!!, property.id!!.toInt())

            when (result) {
                is PropertyResult.DBError -> {
                    PopUpAlert.showAlert("No se pudo conectar a la base de datos, intente de nuevo más tarde: " + result.message, Alert.AlertType.ERROR)
                }
                is PropertyResult.Failure -> {
                    PopUpAlert.showAlert("No se pudo agregar la imagen, intente de nuevo.", Alert.AlertType.ERROR)
                }
                is PropertyResult.Success -> {
                }
                else -> {
                    PopUpAlert.showAlert("Ocurrió un error inesperado", Alert.AlertType.ERROR)
                }
            }
        }

        returnToMainMenu()
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
        return tfTitle.text.isBlank() ||
                tfShortDescription.text.isBlank() ||
                tfFullDescription.text.isBlank() ||
                tfDirection.text.isBlank() ||
                tfPrice.text.isBlank() ||
                tfOwnerEmail.text.isBlank() ||
                tfPropertySize.text.isBlank() ||
                tfNumRooms.text.isBlank() ||
                tfNumBathrooms.text.isBlank()
    }

    private fun wrongFieldsValues (): Boolean {
        val unsafeString = Regex("""[*/\"']+""")
        val number = Regex("""[0-9]+""")
        val email = Regex("""[A-z0-9\.\-_+*]+@[A-z]+\.[A-z]+""")

        if (unsafeString.containsMatchIn(tfTitle.text.trim())) {
            PopUpAlert.showAlert("El campo *Titulo contiene caracteres no soportados: *, /, \", \'", Alert.AlertType.WARNING)
            return true
        }
        if (unsafeString.containsMatchIn(tfDirection.text.trim())) {
            PopUpAlert.showAlert("El campo *Dirección contiene caracteres no soportados: *, /, \", \'", Alert.AlertType.WARNING)
            return true
        }
        if (unsafeString.containsMatchIn(tfFullDescription.text.trim())) {
            PopUpAlert.showAlert("El campo *Descripción completa contiene caracteres no soportados: *, /, \", \'", Alert.AlertType.WARNING)
            return true
        }
        if (unsafeString.containsMatchIn(tfShortDescription.text.trim())) {
            PopUpAlert.showAlert("El campo *Descripción corta contiene caracteres no soportados: *, /, \", \'", Alert.AlertType.WARNING)
            return true
        }
        if (!email.matches(tfOwnerEmail.text.trim())) {
            PopUpAlert.showAlert("El campo *Email no contiene una dirección de email valida, intente de nuevo", Alert.AlertType.WARNING)
            return true
        }
        if (!number.matches(tfPrice.text.trim())) {
            PopUpAlert.showAlert("El campo *Precio no contiene un numero valido, intente de nuevo", Alert.AlertType.WARNING)
            return true
        }
        if (!number.matches(tfPropertySize.text.trim())) {
            PopUpAlert.showAlert("El campo *tamaño no contiene un numero valido, intente de nuevo", Alert.AlertType.WARNING)
            return true
        }
        if (!number.matches(tfNumRooms.text.trim())) {
            PopUpAlert.showAlert("El campo *numero de cuartos no contiene un numero valido, intente de nuevo", Alert.AlertType.WARNING)
            return true
        }
        if (!number.matches(tfNumBathrooms.text.trim())) {
            PopUpAlert.showAlert("El campo *numero de baños no contiene un numero valido, intente de nuevo", Alert.AlertType.WARNING)
            return true
        }

        return false
    }

    private fun fieldValuesTooLong (): Boolean {
        if (tfTitle.text.trim().length > 149) {
            PopUpAlert.showAlert("El campo *Titulo puede contener maximo 150 caracteres", Alert.AlertType.WARNING)
            return true
        }
        if (tfDirection.text.trim().length > 399) {
            PopUpAlert.showAlert("El campo *Dirección puede contener maximo 400 caracteres", Alert.AlertType.WARNING)
            return true
        }
        if (tfFullDescription.text.trim().length > 499) {
            PopUpAlert.showAlert("El campo *Descripción completa puede contener maximo 500 caracteres", Alert.AlertType.WARNING)
            return true
        }
        if (tfShortDescription.text.trim().length > 199) {
            PopUpAlert.showAlert("El campo *Descripción corta puede contener maximo 200 caracteres", Alert.AlertType.WARNING)
            return true
        }
        if (tfOwnerEmail.text.trim().length > 69) {
            PopUpAlert.showAlert("El campo *Email puede contener maximo 70 caracteres", Alert.AlertType.WARNING)
            return true
        }
        if (tfPrice.text.trim().length > 15) {
            PopUpAlert.showAlert("El campo *Precio contiene un numero muy grande", Alert.AlertType.WARNING)
            return true
        }
        if (tfPropertySize.text.trim().length > 5) {
            PopUpAlert.showAlert("El campo *tamaño contiene un numero demasiado grande", Alert.AlertType.WARNING)
            return true
        }
        if (tfNumRooms.text.trim().length > 3) {
            PopUpAlert.showAlert("El campo *numero de cuartos contiene un numero demasiado grande", Alert.AlertType.WARNING)
            return true
        }
        if (tfNumBathrooms.text.trim().length > 3) {
            PopUpAlert.showAlert("El campo *numero de baños contiene un numero demasiado grande", Alert.AlertType.WARNING)
            return true
        }

        return false
    }
}
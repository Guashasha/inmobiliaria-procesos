package GUI

import DTO.Property
import DTO.PropertyAction
import DTO.PropertyState
import GUI.Utility.PopUpAlert
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import main.kotlin.DAO.PropertyDAO
import main.kotlin.DAO.PropertyResult

class ModifyProperty {
    private lateinit var mainPane: BorderPane
    private lateinit var originalPane: Pane
    private lateinit var property: Property
    private lateinit var lbHeader: Label

    @FXML
    private lateinit var tfTitle: TextField
    @FXML
    private lateinit var tfShortDescription: TextField
    @FXML
    private lateinit var tfFullDescription: TextArea
    @FXML
    private lateinit var tfPrice: TextField
    @FXML
    private lateinit var cbAction: ComboBox<String>
    @FXML
    private lateinit var btnSuspend: Button
    @FXML
    private lateinit var btnOccupy: Button

    fun initialize(mainPane: BorderPane, apRoot: AnchorPane, lbHeader: Label, property: Property) {
        this.mainPane = mainPane
        this.originalPane = apRoot
        this.lbHeader = lbHeader
        this.property = property

        cbAction.items.addAll(PropertyAction.sell.toString(), PropertyAction.rent.toString())

        setPropertytext()
        updateButtons()
    }

    private fun setPropertytext () {
        tfTitle.text = property.title
        tfShortDescription.text = property.shortDescription
        tfFullDescription.text = property.fullDescription
        tfPrice.text = property.price.toString()
        cbAction.value = property.action.toString()
    }

    private fun updateButtons () {
        when (property.state) {
            PropertyState.available -> {
                btnOccupy.text = "Marcar propiedad como ocupada"
                btnOccupy.setOnAction { run {
                    changePropertyToOccupied()
                }}
                btnSuspend.text = "Marcar propiedad como supendida"
                btnSuspend.setOnAction { run {
                    changePropertyToSuspended()
                }}
            }
            PropertyState.occupied -> {
                btnOccupy.text = "Marcar propiedad como disponible"
                btnOccupy.setOnAction { run {
                    changePropertyToAvailable()
                }}
                btnSuspend.isDisable = true
            }
            PropertyState.suspended -> {
                btnOccupy.isDisable = true
                btnSuspend.text = "Marcar propiedad como disponible"
                btnSuspend.setOnAction { run {
                    changePropertyToAvailable()
                } }
            }
        }
    }

    fun updateProperty () {
        this.property = createProperty() ?: return
        val dao = PropertyDAO()

        val result = dao.modify(property)

        when (result) {
            is PropertyResult.DBError -> PopUpAlert.showAlert("No pudimos conectarnos con nuestros servicios, intentelo de nuevo más tarde", Alert.AlertType.ERROR)
            is PropertyResult.Failure -> PopUpAlert.showAlert("No se pudo modificar la propiedad, intente de nuevo", Alert.AlertType.ERROR)
            is PropertyResult.NotFound -> PopUpAlert.showAlert("Hubo un error al buscar la propiedad a modificar", Alert.AlertType.ERROR)
            is PropertyResult.Success -> PopUpAlert.showAlert("Se modificaron los datos correctamente", Alert.AlertType.INFORMATION)
            is PropertyResult.WrongProperty -> PopUpAlert.showAlert("Los datos ingresados para la propiedad son incorrectos", Alert.AlertType.WARNING)
            else -> PopUpAlert.showAlert("Ocurrió un error inesperado, intente de nuevo", Alert.AlertType.ERROR)
        }

        updateButtons()
    }

    private fun createProperty (): Property? {
        val title = tfTitle.text.trim()
        val shortDescription = tfShortDescription.text.trim()
        val fullDescription = tfFullDescription.text.trim()
        val price = tfPrice.text.trim()
        val action = PropertyAction.valueOf(cbAction.value.trim())
        val priceNum: Long

        try {
            priceNum = price.toLong()
        }
        catch (error: NumberFormatException) {
            PopUpAlert.showAlert("El precio es un numero incorrecto", Alert.AlertType.ERROR)
            return null;
        }

        val unsafeString = Regex("""[-*/\"'#]+""")

        if (unsafeString.containsMatchIn(title)) {
            PopUpAlert.showAlert("El titulo contiene caracteres no soportados", Alert.AlertType.ERROR)
            return null
        }
        else if (unsafeString.containsMatchIn(shortDescription)) {
            PopUpAlert.showAlert("La descripción corta contiene caracteres no soportados", Alert.AlertType.ERROR)
            return null
        }
        else if (unsafeString.containsMatchIn(fullDescription)) {
            PopUpAlert.showAlert("La descripción completa contiene caracteres no soportados", Alert.AlertType.ERROR)
            return null
        }

        return Property(this.property.id, title, shortDescription, fullDescription, this.property.type, priceNum, this.property.state, this.property.direction, this.property.houseOwner, action, this.property.city, this.property.numRooms, this.property.numBathrooms, this.property.garage, this.property.garden, this.property.size, this.property.image)
    }

    private fun changePropertyToSuspended () {
        when (this.property.state) {
            PropertyState.occupied -> {
                PopUpAlert.showAlert("No puede suspender la propiedad mientras está ocupada", Alert.AlertType.WARNING)
            }
            PropertyState.suspended -> {
                PopUpAlert.showAlert("La propiedad ya se encuentra suspendida", Alert.AlertType.WARNING)
            }
            else -> {
                property.state = PropertyState.suspended
                val dao = PropertyDAO()

                val result = dao.modify(this.property)

                when (result) {
                    is PropertyResult.DBError -> PopUpAlert.showAlert("No pudimos conectarnos con nuestros servicios, intentelo de nuevo más tarde", Alert.AlertType.ERROR)
                    is PropertyResult.Failure -> PopUpAlert.showAlert("No se pudo modificar la propiedad, intente de nuevo", Alert.AlertType.ERROR)
                    is PropertyResult.NotFound -> PopUpAlert.showAlert("Hubo un error al buscar la propiedad a modificar", Alert.AlertType.ERROR)
                    is PropertyResult.Success -> PopUpAlert.showAlert("Se modificaron los datos correctamente", Alert.AlertType.INFORMATION)
                    is PropertyResult.WrongProperty -> PopUpAlert.showAlert("Los datos ingresados para la propiedad son incorrectos", Alert.AlertType.WARNING)
                    else -> PopUpAlert.showAlert("Ocurrió un error inesperado, intente de nuevo", Alert.AlertType.ERROR)
                }
            }
        }

        updateButtons()
    }

    private fun changePropertyToOccupied () {
        property.state = PropertyState.occupied
        val dao = PropertyDAO()

        val result = dao.modify(this.property)

        when (result) {
            is PropertyResult.DBError -> PopUpAlert.showAlert("No pudimos conectarnos con nuestros servicios, intentelo de nuevo más tarde", Alert.AlertType.ERROR)
            is PropertyResult.Failure -> PopUpAlert.showAlert("No se pudo modificar la propiedad, intente de nuevo", Alert.AlertType.ERROR)
            is PropertyResult.NotFound -> PopUpAlert.showAlert("Hubo un error al buscar la propiedad a modificar", Alert.AlertType.ERROR)
            is PropertyResult.Success -> PopUpAlert.showAlert("Se modificaron los datos correctamente", Alert.AlertType.INFORMATION)
            is PropertyResult.WrongProperty -> PopUpAlert.showAlert("Los datos ingresados para la propiedad son incorrectos", Alert.AlertType.WARNING)
            else -> PopUpAlert.showAlert("Ocurrió un error inesperado, intente de nuevo", Alert.AlertType.ERROR)
        }
    }

    private fun changePropertyToAvailable () {
        property.state = PropertyState.available
        val dao = PropertyDAO()

        val result = dao.modify(this.property)

        when (result) {
            is PropertyResult.DBError -> PopUpAlert.showAlert("No pudimos conectarnos con nuestros servicios, intentelo de nuevo más tarde", Alert.AlertType.ERROR)
            is PropertyResult.Failure -> PopUpAlert.showAlert("No se pudo modificar la propiedad, intente de nuevo", Alert.AlertType.ERROR)
            is PropertyResult.NotFound -> PopUpAlert.showAlert("Hubo un error al buscar la propiedad a modificar", Alert.AlertType.ERROR)
            is PropertyResult.Success -> PopUpAlert.showAlert("Se modificaron los datos correctamente", Alert.AlertType.INFORMATION)
            is PropertyResult.WrongProperty -> PopUpAlert.showAlert("Los datos ingresados para la propiedad son incorrectos", Alert.AlertType.WARNING)
            else -> PopUpAlert.showAlert("Ocurrió un error inesperado, intente de nuevo", Alert.AlertType.ERROR)
        }
    }

    fun volverAInformacion () {
        this.lbHeader.text = "Informacion de propiedad"
        this.mainPane.center = originalPane
    }
}
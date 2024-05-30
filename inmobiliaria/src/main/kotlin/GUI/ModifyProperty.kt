package GUI

import DTO.Property
import DTO.PropertyAction
import DTO.PropertyState
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane

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
            PropertyState.available -> TODO()
            PropertyState.occupied -> TODO()
            PropertyState.suspended -> TODO()
        }
    }

    fun updateProperty () {

    }
}
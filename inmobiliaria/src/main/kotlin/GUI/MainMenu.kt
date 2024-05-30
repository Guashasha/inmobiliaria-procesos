package GUI

import DTO.Account
import DTO.AccountType
import DTO.PropertyType
import GUI.Utility.PopUpAlert
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.io.IOException

class MainMenu {
    private lateinit var account: Account
    private lateinit var bpMain: BorderPane
    private lateinit var lbHeader: Label

    @FXML
    private lateinit var mainPane: Pane
    @FXML
    private lateinit var cbPropertyType: ComboBox<String>
    @FXML
    private lateinit var tfSearchQuery: TextField
    @FXML
    private lateinit var btnAgregarPropiedad: Button


    fun initialize(bpMain: BorderPane, account: Account, lbHeader: Label) {
        this.bpMain = bpMain
        this.account = account
        this.lbHeader = lbHeader

        cbPropertyType.items.addAll(PropertyType.all.toString(), PropertyType.building.toString(), PropertyType.house.toString(), PropertyType.apartment.toString(), PropertyType.premises.toString())
        cbPropertyType.value = PropertyType.all.toString()

        if (account.type == AccountType.CLIENT) {
            btnAgregarPropiedad.isVisible = false
        }
    }

    fun openProperties () {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/PropertyList.fxml"))
        var bpPropertyList : BorderPane? = null
        try {
            bpPropertyList = fxmlLoader.load()
        }
        catch (error : IOException) {
            PopUpAlert.showAlert("Error al cargar la ventana de registro", Alert.AlertType.WARNING)
        }
        if (bpPropertyList != null) {
            val propertyListController = fxmlLoader.getController<PropertyList>()
            this.lbHeader.text = "Lista de propiedades"
            this.bpMain.center = bpPropertyList
            val stage = bpMain.scene.window as Stage
            propertyListController.initialize(this.bpMain, this.mainPane, account, this.lbHeader, tfSearchQuery.text, PropertyType.valueOf(cbPropertyType.value))
            stage.title = "Lista propiedades"
        }
    }

    fun openAddProperty () {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/AddProperty.fxml"))
        var bpAddProperty : Pane? = null
        try {
            bpAddProperty = fxmlLoader.load()
        }
        catch (error : IOException) {
            PopUpAlert.showAlert("Error al cargar la ventana de registro", Alert.AlertType.WARNING)
        }
        if (bpAddProperty != null) {
            val propertyListController = fxmlLoader.getController<AddProperty>()
            this.lbHeader.text = "Lista de propiedades"
            this.bpMain.center = bpAddProperty
            val stage = bpMain.scene.window as Stage
            propertyListController.initialize(mainPane, bpMain, lbHeader)
            stage.title = "Lista propiedades"
        }
    }

    fun openEditProfile () {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/EditAccount.fxml"))
        var pnEditAccount : Pane? = null
        try {
            pnEditAccount = fxmlLoader.load()
        }
        catch (error : IOException) {
            PopUpAlert.showAlert("Error al cargar la ventana de registro", Alert.AlertType.WARNING)
        }
        if (pnEditAccount != null) {
            val editAccountController = fxmlLoader.getController<EditAccount>()
            this.lbHeader.text = "Editar perfil"
            this.bpMain.center = pnEditAccount
            val stage = bpMain.scene.window as Stage
            editAccountController.initialize(bpMain, mainPane, account, lbHeader)
            stage.title = "Lista propiedades"
        }
    }
}
package GUI

import DTO.Account
import GUI.Utility.PopUpAlert
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.io.IOException

class MainMenu {
    private lateinit var account: Account
    private lateinit var bpMain: BorderPane
    private lateinit var lbHeader: Label
    @FXML
    private lateinit var mainAnchorPaneMenu: AnchorPane


    fun initialize(bpMain: BorderPane, account: Account, lbHeader : Label) {
        this.bpMain = bpMain
        this.account = account
        this.lbHeader = lbHeader
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
            propertyListController.initialize(bpMain, mainAnchorPaneMenu, account, lbHeader)
            stage.title = "Lista propiedades"
        }
    }

    fun openScheduleVisit () {

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
            editAccountController.initialize(bpMain, mainAnchorPaneMenu, account, lbHeader)
            stage.title = "Lista propiedades"
        }
    }
}
package GUI

import DAO.AccountDAO
import DAO.AccountResult
import DTO.Account
import GUI.Utility.PopUpAlert
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane

class EditAccount {
    @FXML
    private lateinit var tfEmail: TextField
    @FXML
    private lateinit var tfName: TextField
    @FXML
    private lateinit var tfNumber: TextField

    private lateinit var account: Account
    private lateinit var bpMain: BorderPane
    private lateinit var mainAnchorPaneMenu: Pane
    private lateinit var lbHeader: Label

    private fun setLabel() {
        tfName.promptText = this.account.name
        tfEmail.promptText = this.account.email
        tfNumber.promptText = this.account.phone
    }



    private fun getData(): Account {
        val name = if (tfName.text.isNullOrBlank()) this.account.name else tfName.text
        val numberPhone = if (tfNumber.text.isNullOrBlank()) this.account.phone else tfNumber.text
        val email = if (tfEmail.text.isNullOrBlank()) this.account.email else tfEmail.text

        return Account (
            id = this.account.id,
            name = name,
            phone = numberPhone,
            email = email,
            type = this.account.type,
            password = null
        )
    }

    @FXML
    fun HandleSave () {
            val modifyAccount = getData()
            val accountDAO = AccountDAO()
            val result = accountDAO.modify(modifyAccount)
            when (result) {
                is AccountResult.DBError -> {
                    PopUpAlert.showAlert("Error en la conexión en la base de datos", Alert.AlertType.ERROR)
                }
                is AccountResult.Failure -> {
                    PopUpAlert.showAlert("El correo electrónico ya esta registrado, por favor ingrese otro distinto", Alert.AlertType.WARNING)
                }
                is AccountResult.Success -> {
                    this.account = getData()
                    PopUpAlert.showAlert("Se modificarón los datos de la cuenta exitosamente", Alert.AlertType.INFORMATION)
                }
                is AccountResult.WrongAccount -> {
                    PopUpAlert.showAlert("Error en el id de la cuenta", Alert.AlertType.ERROR)
                }

                else -> {
                    PopUpAlert.showAlert("Error desconocido", Alert.AlertType.ERROR)
                }
            }

    }

    fun initialize(bpMain: BorderPane, mainAnchorPaneMenu: Pane, account: Account, lbHeader: Label) {
        this.bpMain = bpMain
        this.mainAnchorPaneMenu = mainAnchorPaneMenu
        this.account = account
        this.lbHeader = lbHeader
        setLabel()
    }

    @FXML
    fun backToMainMenu () {
        this.lbHeader.text = "Menu principal"
        this.bpMain.center = mainAnchorPaneMenu
    }

    @FXML
    fun backArrow () {
        if (isAcepted())
            backToMainMenu()
    }

    private fun isAcepted () : Boolean{
        return PopUpAlert.showConfirmationDialog("¿Seguro que desea salir? Los cambios no se realizaran")
    }

}
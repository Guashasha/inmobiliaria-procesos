package GUI

import DAO.AccountDAO
import DAO.AccountResult
import DTO.Account
import DTO.AccountType
import GUI.Utility.PopUpAlert
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.io.IOException


class EditAccount {
    @FXML
    private lateinit var tfEmail: TextField

    @FXML
    private lateinit var tfName: TextField

    @FXML
    private lateinit var tfNumber: TextField

    @FXML
    private lateinit var btnEliminar: Button

    @FXML
    private lateinit var pwPassword: PasswordField

    @FXML
    private lateinit var pwReapeatPassword: PasswordField

    private lateinit var account: Account
    private lateinit var bpMain: BorderPane
    private lateinit var mainAnchorPaneMenu: Pane
    private lateinit var lbHeader: Label

    fun setLabel() {
        tfName.promptText = this.account.name
        tfEmail.promptText = this.account.email
        tfNumber.promptText = this.account.phone
        configurePhoneNumberField()
    }


    private fun getData(): Account {
        val name = if (tfName.text.isNullOrBlank()) this.account.name else tfName.text
        val numberPhone = if (tfNumber.text.isNullOrBlank()) this.account.phone else tfNumber.text
        val email = if (tfEmail.text.isNullOrBlank()) this.account.email else tfEmail.text
        var password = this.account.password
        if (pwPassword.text.isNullOrBlank()) {
            if (!pwPassword.text.equals(pwReapeatPassword.text)) {
                throw IllegalArgumentException("Las contraseñas no son iguales")
            }
            password = pwPassword.text
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!email.matches(emailRegex)) {
            throw IllegalArgumentException("El correo electrónico no es válido")
        }

        return Account(
            id = this.account.id,
            name = name,
            phone = numberPhone,
            email = email,
            type = this.account.type,
            password = password
        )
    }

    private fun configurePhoneNumberField() {
        tfNumber.textFormatter = TextFormatter<String> { change ->
            val newText = change.controlNewText
            if (newText.matches(Regex("[0-9]*")) && newText.length <= 10) {
                change
            } else {
                null
            }
        }
    }

    @FXML
    fun HandleSave() {
        if (isAcepted("¿Seguro que desea modificar su cuenta?")) {
            try {
                val modifyAccount = getData()
                val accountDAO = AccountDAO()
                val result = accountDAO.modify(modifyAccount)
                when (result) {
                    is AccountResult.DBError -> {
                        PopUpAlert.showAlert("Error en la conexión en la base de datos", Alert.AlertType.ERROR)
                    }

                    is AccountResult.Failure -> {
                        PopUpAlert.showAlert(
                            "El correo electrónico ya esta registrado, por favor ingrese otro distinto",
                            Alert.AlertType.WARNING
                        )
                    }

                    is AccountResult.Success -> {
                        PopUpAlert.showAlert(
                            "Se modificarón los datos de la cuenta exitosamente",
                            Alert.AlertType.INFORMATION
                        )
                        this.account = modifyAccount
                        setLabel()
                    }

                    is AccountResult.WrongAccount -> {
                        PopUpAlert.showAlert("Error en el id de la cuenta", Alert.AlertType.ERROR)
                    }

                    else -> {
                        PopUpAlert.showAlert("Error desconocido", Alert.AlertType.ERROR)
                    }
                }
            } catch (e: IllegalArgumentException) {
                PopUpAlert.showAlert(e.message ?: "", Alert.AlertType.ERROR)
            }
        }
    }

    fun initialize(bpMain: BorderPane, mainAnchorPaneMenu: Pane, account: Account, lbHeader: Label) {
        this.bpMain = bpMain
        this.mainAnchorPaneMenu = mainAnchorPaneMenu
        this.account = account
        if (this.account.type == AccountType.AGENT) {
            btnEliminar.isVisible = false
        }
        this.lbHeader = lbHeader
        setLabel()
    }

    @FXML
    fun backToMainMenu() {
        openMainMenu()
    }

    @FXML
    fun backArrow() {
        if (isAcepted("¿Seguro que desea salir? Los cambios no se realizarán"))
            backToMainMenu()
    }

    private fun isAcepted(contenido: String): Boolean {
        return PopUpAlert.showConfirmationDialog(contenido)
    }


    @FXML
    fun deleteAccount() {
        if (isAcepted("¿Seguro que desea borrar su cuenta en el sistema?\nEsta acción no se puede revertir")) {
            val accountDAO = AccountDAO()
            val result = accountDAO.delete(this.account)
            when (result) {
                is AccountResult.DBError -> {
                    PopUpAlert.showAlert("Error en la conexión en la base de datos", Alert.AlertType.ERROR)
                }

                is AccountResult.Failure -> {

                }

                is AccountResult.Success -> {
                    PopUpAlert.showAlert("La cuenta ha sido eliminada con éxito", Alert.AlertType.INFORMATION)
                    openLogin()
                }

                is AccountResult.WrongAccount -> {
                    PopUpAlert.showAlert("Error en el id de la cuenta", Alert.AlertType.ERROR)
                }

                else -> {
                    PopUpAlert.showAlert("Error desconocido", Alert.AlertType.ERROR)
                }
            }
        }

    }

    private fun openMainMenu() {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/MainMenu.fxml"))
        var aPaneMainMenu: Pane? = null
        try {
            aPaneMainMenu = fxmlLoader.load()
        } catch (error: IOException) {
            print(error.message)
            PopUpAlert.showAlert("Error al cargar la ventana principal", Alert.AlertType.WARNING)
        }
        if (aPaneMainMenu != null) {
            val mainMenuController = fxmlLoader.getController<MainMenu>()
            this.lbHeader.text = "Propiedades Xalapa Nueva Generación"
            this.bpMain.center = aPaneMainMenu
            val stage = bpMain.scene.window as Stage
            mainMenuController.initialize(bpMain, account, lbHeader)
            stage.title = "Menu principal"
        }
    }

    private fun openLogin() {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/Login.fxml"))
        var paneLogin: BorderPane? = null
        try {
            paneLogin = fxmlLoader.load()
        } catch (error: IOException) {
            print(error.message)
            PopUpAlert.showAlert("Error al cargar el login", Alert.AlertType.WARNING)
        }

        if (paneLogin != null) {
            val stage = bpMain.scene.window as Stage
            stage.scene.root = paneLogin
            stage.title = "Login"
        }
    }
}
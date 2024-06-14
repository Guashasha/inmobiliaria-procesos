package GUI

import DAO.AccountDAO
import DAO.AccountResult
import DTO.Account
import DTO.AccountType
import GUI.Utility.PopUpAlert
import javafx.event.ActionEvent
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
    private lateinit var tfLastName: TextField
    @FXML
    private lateinit var tfNumber: TextField
    @FXML
    private lateinit var btnEliminar: Button
    @FXML
    private lateinit var pwPassword: PasswordField
    @FXML
    private lateinit var pwReapeatPassword: PasswordField
    @FXML
    private lateinit var tfShowedRepeatPassword: TextField
    @FXML
    private lateinit var tfshowedPassword: TextField
    @FXML
    private lateinit var tgPassword: ToggleButton
    private lateinit var account: Account
    private lateinit var bpMain: BorderPane
    private lateinit var mainAnchorPaneMenu: Pane
    private lateinit var lbHeader: Label

    fun setLabel() {
        tfName.text = this.account.name
        tfLastName.text = this.account.lastName
        tfEmail.text = this.account.email
        tfNumber.text = this.account.phone
        tfName.promptText = this.account.name
        tfLastName.promptText = this.account.lastName
        tfEmail.promptText = this.account.email
        tfNumber.promptText = this.account.phone
        configurePhoneNumberField()
        configureNameField()
        configureLastNameField()
    }

    private fun configureTextField(textField: TextField, regex: Regex, maxLength: Int) {
        textField.textFormatter = TextFormatter<String> { change ->
            val newText = change.controlNewText
            if (newText.matches(regex) && newText.length <= maxLength) {
                change
            } else {
                null
            }
        }
    }

    private fun configureNameField() {
        val nameRegex = Regex("^[A-Za-zÀ-ÿ\\s]*$")
        val maxLength = 50
        configureTextField(tfName, nameRegex, maxLength)
    }

    private fun configureLastNameField() {
        val lastNameRegex = Regex("^[A-Za-zÀ-ÿ\\s]*$")
        val maxLength = 50
        configureTextField(tfLastName, lastNameRegex, maxLength)
    }

    private fun getData(): Account {
        val name = if (tfName.text.isNullOrBlank()) this.account.name else tfName.text
        val lastname = if (tfLastName.text.isNullOrBlank()) this.account.name else tfLastName.text
        val numberPhone = if (tfNumber.text.isNullOrBlank()) this.account.phone else tfNumber.text
        val email = if (tfEmail.text.isNullOrBlank()) this.account.email else tfEmail.text

        if (!pwPassword.text.equals(pwReapeatPassword.text)) {
            throw IllegalArgumentException("Las contraseñas no son iguales")
        }
        val password = if (pwPassword.text.isNullOrBlank()) this.account.password else pwPassword.text

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!email.matches(emailRegex)) {
            throw IllegalArgumentException("El correo electrónico no es válido")
        }

        return Account(
            id = this.account.id,
            name = name,
            lastName = lastname,
            phone = numberPhone,
            email = email,
            type = this.account.type,
            password = password
        )
    }

    @FXML
    fun toggleAction(event: ActionEvent) {
        if (tgPassword.isSelected) {
            tfshowedPassword.text = pwPassword.text
            tfShowedRepeatPassword.text = pwReapeatPassword.text
            pwPassword.isVisible = false
            pwReapeatPassword.isVisible = false
            tfshowedPassword.isVisible = true
            tfShowedRepeatPassword.isVisible = true
        } else {
            pwPassword.text = tfshowedPassword.text
            pwReapeatPassword.text = tfShowedRepeatPassword.text
            pwPassword.isVisible = true
            pwReapeatPassword.isVisible = true
            tfshowedPassword.isVisible = false
            tfShowedRepeatPassword.isVisible = false
        }
    }

    private fun validatePasswordSecurity(password: String) {
        val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$".toRegex()
        if (!password.matches(passwordRegex)) {
            throw IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, una letra mayúscula, una letra minúscula, un número y un carácter especial")
        }
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
        syncPasswordFields()
        if (this.account.type == AccountType.AGENT) {
            this.btnEliminar.isVisible = false
        }
    }

    @FXML
    fun backToMainMenu() {
        openMainMenu()
    }

    @FXML
    fun backArrow() {
        //if (isAcepted("¿Seguro que desea salir? Los cambios no se realizarán"))
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

    private fun syncPasswordFields() {
        pwPassword.textProperty().addListener { _, _, newValue ->
            if (!tgPassword.isSelected) {
                tfshowedPassword.text = newValue
            }
        }

        tfshowedPassword.textProperty().addListener { _, _, newValue ->
            if (tgPassword.isSelected) {
                pwPassword.text = newValue
            }
        }

        pwReapeatPassword.textProperty().addListener { _, _, newValue ->
            if (!tgPassword.isSelected) {
                tfShowedRepeatPassword.text = newValue
            }
        }

        tfShowedRepeatPassword.textProperty().addListener { _, _, newValue ->
            if (tgPassword.isSelected) {
                pwReapeatPassword.text = newValue
            }
        }
    }
}
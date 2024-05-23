package GUI

import DTO.Account
import DTO.AccountType
import DAO.AccountDAO
import DAO.AccountResult
import GUI.Utility.PopUpAlert
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.stage.Stage

class CreateAccount {
    @FXML
    private lateinit var pwPassword: PasswordField
    @FXML
    private lateinit var pwRepeatPassword: PasswordField
    @FXML
    private lateinit var tfEmail: TextField
    @FXML
    private lateinit var tfLastName: TextField
    @FXML
    private lateinit var tfName: TextField
    @FXML
    private lateinit var tfPhoneNumber: TextField
    private lateinit var mainPane: BorderPane
    private lateinit var originalPane: Pane

    fun initialize(mainPane: BorderPane, originalPane: Pane) {
        this.mainPane = mainPane
        this.originalPane = originalPane
    }

    private fun obtenerDatosDeCuenta(): Account {
        val name = tfName.text
        val lastName = tfLastName.text
        val email = tfEmail.text
        val phoneNumber = tfPhoneNumber.text
        val password = pwPassword.text
        val repeatPassword = pwRepeatPassword.text

        if (password != repeatPassword) {
            throw IllegalArgumentException("Las contraseñas no coinciden")
        }

        val accountType = AccountType.CLIENT

        return Account(
            id = null,
            email = email,
            type = accountType,
            name = "$name $lastName",
            phone = phoneNumber,
            password = password
        )
    }

    @FXML
    fun handleConfirm() {
        try {
            val account = obtenerDatosDeCuenta()
            val accountDAO = AccountDAO()
            val result = accountDAO.add(account)
            when (result) {
                is AccountResult.Success -> {
                    PopUpAlert.showAlert("Cuenta creada exitosamente", Alert.AlertType.INFORMATION)
                    backToLogin()
                }
                is AccountResult.Failure -> {
                    PopUpAlert.showAlert("No se pudo crear la cuenta", Alert.AlertType.ERROR)
                }
                is AccountResult.WrongAccount -> {
                    PopUpAlert.showAlert("Datos de la cuenta incorrectos", Alert.AlertType.WARNING)
                }
                is AccountResult.DBError -> {
                    PopUpAlert.showAlert("Error en la base de datos: ${result.message}", Alert.AlertType.ERROR)
                    backToLogin()
                }
                else -> {
                    PopUpAlert.showAlert("Error desconocido", Alert.AlertType.ERROR)
                }
            }
        } catch (e: IllegalArgumentException) {
            PopUpAlert.showAlert(e.message ?: "", Alert.AlertType.ERROR)
        }
    }

    @FXML
    fun handleCancel() {
        val itsAccept = PopUpAlert.showConfirmationDialog("¿Seguro que desea cancelar su registro de cuenta?")
        if (itsAccept) {
            backToLogin()
        }
    }

    @FXML
    fun backToLogin() {
        mainPane.center = originalPane
        val stage = mainPane.scene.window as Stage
        val hbox = mainPane.top as HBox
        val label = hbox.children?.filterIsInstance<Label>()?.find { it.id == "lbHeader" }
        if (label != null) {
            label.text = "Propiedades Xalapa Nueva Generación"
        }
        stage.title = "Inicio de sesión"
    }
}
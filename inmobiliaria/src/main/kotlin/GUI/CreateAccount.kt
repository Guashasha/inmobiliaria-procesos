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
        configurePhoneNumberField()
    }

    private fun configurePhoneNumberField() {
        tfPhoneNumber.textFormatter = TextFormatter<String> { change ->
            val newText = change.controlNewText
            if (newText.matches(Regex("[0-9]*")) && newText.length <= 10) {
                change
            } else {
                null
            }
        }
    }

    private fun validateFields() {
        val fields = mapOf(
            "Nombre" to tfName.text.trim(),
            "Apellido" to tfLastName.text.trim(),
            "Correo electrónico" to tfEmail.text.trim(),
            "Número de teléfono" to tfPhoneNumber.text.trim(),
            "Contraseña" to pwPassword.text,
            "Repetir contraseña" to pwRepeatPassword.text
        )

        for ((fieldName, fieldValue) in fields) {
            validateField(fieldValue, fieldName)
        }

        val email = fields["Correo electrónico"]
        if (email != null) {
            validateEmail(email)
        }

        val password = fields["Contraseña"]
        if (password != null) {
            //validatePasswordSecurity(password)
        }

        val repeatPassword = fields["Repetir contraseña"]
        if (password != repeatPassword) {
            throw IllegalArgumentException("Las contraseñas no coinciden")
        }
    }

    private fun validateField(field: String, fieldName: String) {
        if (field.isBlank()) {
            throw IllegalArgumentException("El campo $fieldName no puede estar vacío")
        }
    }

    private fun validateEmail(email: String) {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!email.matches(emailRegex)) {
            throw IllegalArgumentException("El correo electrónico no es válido")
        }
    }

//    private fun validatePasswordSecurity(password: String) {
//        val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$".toRegex()
//        if (!password.matches(passwordRegex)) {
//            throw IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, una letra mayúscula, una letra minúscula, un número y un carácter especial")
//        }
//    }

    private fun getData(): Account {
        validateFields()

        val name = tfName.text.trim()
        val lastName = tfLastName.text.trim()
        val email = tfEmail.text.trim()
        val phoneNumber = tfPhoneNumber.text.trim()
        val password = pwPassword.text

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
            val account = getData()
            val accountDAO = AccountDAO()
            val result = accountDAO.add(account)
            when (result) {
                is AccountResult.Success -> {
                    PopUpAlert.showAlert("Cuenta creada exitosamente", Alert.AlertType.INFORMATION)
                    backToLogin()
                }
                is AccountResult.Failure -> {
                    PopUpAlert.showAlert("El correo electrónico ya se encuentra registrado", Alert.AlertType.ERROR)
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
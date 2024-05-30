package GUI

import DAO.AccountDAO
import DAO.AccountResult
import DTO.Account
import DTO.AccountType
import javafx.application.Application
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.io.IOException
import GUI.Utility.PopUpAlert
import javafx.scene.layout.AnchorPane
import java.util.*

fun main (args: Array<String>) {
    Application.launch(Login::class.java)
}


class Login : Application() {
    @FXML
    private lateinit var btnCreateAccount: Button
    @FXML
    private lateinit var pwPassword: PasswordField
    @FXML
    private lateinit var tfEmail: TextField
    @FXML
    private lateinit var btnSession: Button
    @FXML
    private lateinit var lbHeader: Label
    @FXML
    private lateinit var bpMain: BorderPane
    @FXML

    private lateinit var pnMain: Pane
    private lateinit var account: Account

    @FXML
    fun openCreateAccount() {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/CreateAccount.fxml"))
        var pnCreateAccount: Pane? = null
        try {
            pnCreateAccount = fxmlLoader.load()
        } catch (error: IOException) {
            PopUpAlert.showAlert("Error al cargar la ventana de registro", Alert.AlertType.WARNING)
        }
        if (pnCreateAccount != null) {
            val createAccountController = fxmlLoader.getController<CreateAccount>()
            this.lbHeader.text = "Crear Cuenta"
            this.bpMain.center = pnCreateAccount
            val stage = bpMain.scene.window as Stage
            createAccountController.initialize(bpMain, pnMain)
            stage.title = "Crear Cuenta"
        }
    }


    override fun start (primaryStage: Stage) {
        try {
            val parent: Parent? = FXMLLoader.load<Parent>(javaClass.getResource("/FXML/Login.fxml"))

            primaryStage.run {
                title = "Inicio de sesión"

                scene = Scene(parent)
                show()
            }
        }
        catch (error: IOException) {
            throw RuntimeException(error)
        }
    }

    @FXML
    fun handleSession () {
        if (validateCredentials())
            openViewForType()
    }


    private fun openAgentView () {
        openMainMenu()
    }

    private fun openClientView () {
        openMainMenu()
    }

    private fun openViewForType () {
        when (this.account.type) {
            AccountType.CLIENT -> openClientView()
            AccountType.AGENT -> openAgentView()
        }
    }

    private fun validateCredentials(): Boolean {
        val email = tfEmail.text
        val password = pwPassword.text
        val accountDAO = AccountDAO()
        val result = accountDAO.validateCredentials(email, password)

        return when (result) {
            is AccountResult.Found -> {
                this.account = result.account
                true
            }
            is AccountResult.NotFound -> {
                PopUpAlert.showAlert("El correo electrónico o la contraseña son incorrectos", Alert.AlertType.INFORMATION)
                false
            }
            is AccountResult.WrongAccount -> {
                PopUpAlert.showAlert("No se ingresaron credenciales válidas", Alert.AlertType.WARNING)
                false
            }
            is AccountResult.DBError -> {
                PopUpAlert.showAlert("Error en la conexión a la base de datos", Alert.AlertType.ERROR)
                false
            }
            is AccountResult.Success -> {
                true
            }
            else -> {
                false
            }
        }
    }

    private fun openMainMenu () {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/MainMenu.fxml"))
        var aPaneMainMenu  : Pane? = null
        try {
            aPaneMainMenu = fxmlLoader.load()
        }
        catch (error: IOException) {
            print(error.message)
            PopUpAlert.showAlert("Error al cargar la ventana principal", Alert.AlertType.WARNING)
        }
        if (aPaneMainMenu != null) {
            val mainMenuController = fxmlLoader.getController<MainMenu>()
            this.lbHeader.text = "Menu principal"
            this.bpMain.center = aPaneMainMenu
            val stage = bpMain.scene.window as Stage
            mainMenuController.initialize(bpMain, account, lbHeader)
            stage.title = "Menu principal"
        }

    }

}
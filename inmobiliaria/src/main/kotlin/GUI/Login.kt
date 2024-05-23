package GUI

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

fun main (args: Array<String>) {
    Application.launch(Login::class.java)
}


class Login : Application() {
    @FXML
    private lateinit var btnCreateAccount: Button

    @FXML
    private lateinit var btnEmail: TextField

    @FXML
    private lateinit var btnPassword: PasswordField

    @FXML
    private lateinit var btnSession: Button

    @FXML
    private lateinit var hpForgotPassword: Hyperlink

    @FXML
    private lateinit var lbHeader: Label
    @FXML
    private lateinit var bpMain: BorderPane
    @FXML
    private lateinit var pnMain: Pane


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



}
package GUI.Utility
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType

object PopUpAlert {
    fun showAlert(message: String, type: Alert.AlertType) {
        val alert = Alert(type)
        alert.title = type.toString()
        alert.headerText = null
        alert.contentText = message
        alert.showAndWait()
    }

    fun showConfirmationDialog(message: String): Boolean {
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        alert.title = "Confirmación"
        alert.headerText = null
        alert.contentText = message

        val result = alert.showAndWait()
        return result.get() == ButtonType.OK
    }
}
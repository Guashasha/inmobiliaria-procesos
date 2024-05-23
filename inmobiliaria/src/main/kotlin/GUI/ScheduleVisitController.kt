package GUI

import javafx.application.Application
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.DatePicker
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import java.io.IOException

fun main (args: Array<String>) {
    Application.launch(ScheduleVisitController::class.java)
}

class ScheduleVisitController : Application() {
    @FXML lateinit var gpHorarios : GridPane
    @FXML lateinit var dpFecha : DatePicker
    var idProperty : UInt = 0U
    val listaHorarios = mutableListOf("08:00:00","09:00:00","10:00:00","11:00:00","12:00:00","13:00:00","14:00:00","15:00:00","16:00:00")
    override fun start (primaryStage: Stage) {
        try {
            val parent: Parent? = FXMLLoader.load<Parent>(javaClass.getResource("/FXML/ScheduleVisit.fxml"))

            primaryStage.run {
                title = "Agendar visita"
                scene = Scene(parent)
                show()
            }
        }
        catch (error: IOException) {
            throw RuntimeException(error)
        }
    }

    @FXML fun salir () {

    }

    @FXML fun buscarHorarios () {
        
    }



}
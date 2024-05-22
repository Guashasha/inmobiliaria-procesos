package GUI;

import javafx.application.Application;
import javafx.application.Application.launch
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException

fun main (args: Array<String>) {
    launch(PropertyInfo::class.java)
}

class PropertyInfo : Application() {

    override fun start (primaryStage: Stage) {
        try {
            val parent: Parent? = FXMLLoader.load<Parent>(javaClass.getResource("/FXML/PropertyInfo.fxml"))

            primaryStage.run {
                title = "Todas las propiedades"

                scene = Scene(parent)
                show()
            }
        }
        catch (error: IOException) {
            throw RuntimeException(error)
        }
    }
}

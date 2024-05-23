package GUI

import DAO.VisitDAO
import DAO.VisitResult
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Label
import main.kotlin.DTO.Visit
import java.sql.Date
import java.sql.Time

class ScheduleVisitItemController {
    @FXML lateinit var lbTime : Label
    var propertyId : UInt = 0U
    var clientId : UInt = 0U
    var date : Date? = null
    var parentController : ScheduleVisitController? = null

    @FXML fun agendar () {
        if (date != null && parentController != null) {
            val visitDao = VisitDAO()
            val time = Time.valueOf(lbTime.text)
            val visitResult = visitDao.add(Visit(clientId = clientId, propertyId = propertyId, date = date!!, time = time))

            when (visitResult) {
                is VisitResult.Success -> {
                    showAlert(visitResult.message,Alert.AlertType.INFORMATION)
                    parentController!!.searchSchedule()
                }
                is VisitResult.DBError -> showAlert(visitResult.message,Alert.AlertType.ERROR)
                is VisitResult.Failure -> showAlert(visitResult.message,Alert.AlertType.ERROR)
                is VisitResult.WrongVisit -> showAlert(visitResult.message,Alert.AlertType.WARNING)
                else -> showAlert("Algo salió mal. Intentalo de nuevo más tarde",Alert.AlertType.ERROR)
            }
        } else showAlert("Algo salió mal. Contacte a un técnico",Alert.AlertType.ERROR)
    }

    private fun showAlert (message : String, type : Alert.AlertType) {
        val alert = Alert(type)
        alert.contentText = message
        alert.show()
    }

}
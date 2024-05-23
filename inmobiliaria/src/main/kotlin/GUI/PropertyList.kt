package GUI

import DTO.Account
import DTO.AccountType
import DTO.Property
import DTO.PropertyType
import GUI.Utility.PopUpAlert
import javafx.application.Application
import javafx.application.Application.launch
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.text.Text
import javafx.stage.Stage
import main.kotlin.DAO.PropertyDAO
import main.kotlin.DAO.PropertyResult
import java.io.IOException

fun main (args: Array<String>) {
    launch(PropertyList::class.java)
}

class PropertyList : Application() {
    private lateinit var mainPane: BorderPane
    private lateinit var mainAnchorPaneMenu: AnchorPane
    private lateinit var account: Account
    private lateinit var lbHeader: Label

    @FXML
    lateinit var vboxProperties: VBox

    lateinit var bpMain: BorderPane
    private val query: String? = null
    private val propertyType: PropertyType = PropertyType.all

    override fun start(primaryStage: Stage) {
        try {
            val parent: Parent? = FXMLLoader.load<Parent>(javaClass.getResource("/FXML/PropertyList.fxml"))

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

    fun initialize (bpMain: BorderPane,  mainAnchorPaneMenu: AnchorPane, account: Account, lbHeader: Label) {
        this.bpMain = bpMain
        this.mainAnchorPaneMenu = mainAnchorPaneMenu
        this.account = account
        this.lbHeader = lbHeader
        setProperties()
    }

    private fun setProperties () {
        val properties = getProperties()

        for (property in properties) {
            val pane = HBox()
            pane.spacing = 20.0
            
            val image = ImageView()

            val info = VBox()
            info.spacing = 10.0
            val title = Text(property.title)
            val description = Text(property.shortDescription)
            info.children.addAll(title, description)

            val infoPrice = VBox()
            infoPrice.spacing = 10.0
            val price = Text(property.price.toString())
            val action = Text(property.action.toString())
            infoPrice.children.addAll(action, price)

            val details = Button("Ver detalles")
            details.setOnAction {
                run {
                    when (account.type) {
                        AccountType.CLIENT -> openPropertyInfo(property)
                        AccountType.AGENT -> openAdminPropertyInfo(property)
                    }
                }
            }

            pane.children.addAll(info, infoPrice, details)

            vboxProperties.children.add(pane)
        }
    }

    private fun openPropertyInfo (property: Property) {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/PropertyInfo.fxml"))
        var bpPropertyList : AnchorPane? = null

        try {
            bpPropertyList = fxmlLoader.load()
        }
        catch (error : IOException) {
            PopUpAlert.showAlert("Error al cargar la ventana de información", Alert.AlertType.WARNING)
        }

        if (bpPropertyList != null) {
            val propertyInfoController = fxmlLoader.getController<PropertyInfo>()
            this.lbHeader.text = "Información de propiedad"
            this.bpMain.center = bpPropertyList
            val stage = bpMain.scene.window as Stage
            propertyInfoController.initialize(bpMain, mainAnchorPaneMenu, account, lbHeader, property)
            stage.title = "Información de propiedad"
        }
    }

    private fun openAdminPropertyInfo (property: Property) {

    }

    private fun getProperties (): ArrayList<Property> {
        val dao = PropertyDAO()
        val result = dao.getByQuery(query?: "", propertyType)

        val list = when (result) {
            is PropertyResult.FoundList<*> -> result.list as ArrayList<Property>
            else -> ArrayList()
        }

        return list
    }

    fun returnToList () {
        this.lbHeader.text = "Lista de propiedades"
        this.bpMain.center = mainAnchorPaneMenu
    }
}
package GUI

import DTO.Account
import DTO.AccountType
import DTO.Property
import DTO.PropertyType
import GUI.Utility.PopUpAlert
import javafx.application.Application
import javafx.application.Application.launch
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollBar
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
    private lateinit var mainAnchorPaneMenu: Pane
    private lateinit var account: Account
    private lateinit var lbHeader: Label
    lateinit var bpMain: BorderPane

    @FXML
    lateinit var vboxProperties: VBox
    @FXML
    lateinit var scroll: ScrollBar

    private var query: String? = null
    private var propertyType: PropertyType = PropertyType.all

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

    fun initialize (mainWindow: BorderPane, previousPane: Pane, account: Account, lbHeader: Label, query: String, propertyType: PropertyType) {
        this.bpMain = mainWindow
        this.mainAnchorPaneMenu = previousPane
        this.account = account
        this.lbHeader = lbHeader
        this.query = query
        this.propertyType = propertyType

        setProperties()
    }

    private fun getImages (property: Property) {
        val dao = PropertyDAO()
        val result = dao.getImage(property.id!!)

        property.image = when (result) {
            is PropertyResult.FoundImage -> result.image
            else -> null
        }
    }

    private fun setProperties () {
        val properties = getProperties()

        for (property in properties) {
            val pane = HBox()
            pane.style = "-fx-background-color: linear-gradient(to bottom, #AFA9F3 29.8%, #AFA9F3 50%, #9F99E3 90%); -fx-background-radius: 25px;"
            pane.prefWidth(1200.0)
            pane.padding = Insets(15.0)
            pane.spacing = 20.0

            getImages(property)
            val image = ImageView()
            image.image = property.image

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

            pane.children.addAll(image, info, infoPrice, details)

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
        val fxmlLoader = FXMLLoader(javaClass.getResource("/FXML/AdminPropertyInfo.fxml"))
        var bpPropertyList : AnchorPane? = null

        try {
            bpPropertyList = fxmlLoader.load()
        }
        catch (error : IOException) {
            PopUpAlert.showAlert("Error al cargar la ventana de información", Alert.AlertType.WARNING)
        }

        if (bpPropertyList != null) {
            val propertyInfoController = fxmlLoader.getController<AdminPropertyInfo>()
            this.lbHeader.text = "Información de propiedad"
            this.bpMain.center = bpPropertyList
            val stage = bpMain.scene.window as Stage
            propertyInfoController.initialize(bpMain, mainAnchorPaneMenu, account, lbHeader, property)
            stage.title = "Información de propiedad"
        }
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

    fun returnToMenu () {
        this.lbHeader.text = "Menu principal"
        this.bpMain.center = mainAnchorPaneMenu
    }
}
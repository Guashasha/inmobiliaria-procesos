package GUI

import DTO.Account
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane

class EditAccount {
    @FXML
    private lateinit var tfEmail: TextField
    @FXML
    private lateinit var tfLastName: TextField
    @FXML
    private lateinit var tfName: TextField
    @FXML
    private lateinit var tfPassword: TextField

    private lateinit var account: Account
    private lateinit var bpMain: BorderPane
    private lateinit var mainAnchorPaneMenu: AnchorPane
    private lateinit var lbHeader: Label


    fun initialize(bpMain: BorderPane, mainAnchorPaneMenu: AnchorPane, account: Account, lbHeader : Label) {
        this.bpMain = bpMain
        this.mainAnchorPaneMenu = mainAnchorPaneMenu
        this.account = account
        this.lbHeader = lbHeader
    }

    @FXML
    fun backToMainMenu () {
        this.lbHeader.text = "Menu principal"
        this.bpMain.center = mainAnchorPaneMenu

    }

}
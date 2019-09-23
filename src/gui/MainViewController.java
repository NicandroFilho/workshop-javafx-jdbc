package gui;

import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import application.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML
    private MenuItem menuItemSeller;
    @FXML
    private MenuItem menuItemDepartment;
    @FXML
    private MenuItem menuItemAbout;

    @FXML
    public void onMenuItemSellerAction(){
        // TODO: 23/09/2019
        System.out.println("onMenuItemSellerAction");
    }

    @FXML
    public void onMenuItemDepartmentAction(){
        //load the View of Department
        loadView("/gui/DepartmentList.fxml");
    }

    @FXML
    public void onMenuItemAboutAction(){
        //load the View of About
        loadView("/gui/About.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO: 23/09/2019
    }

    private synchronized void loadView(String absoluteName){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            VBox newVBox = loader.load();

            Scene mainScene = Main.getMainScene();
            VBox mainVBox = (VBox) (((ScrollPane)mainScene.getRoot()).getContent()); //get the main VBox

            Node mainMenu = mainVBox.getChildren().get(0); //stores the firs children of main VBox. In this case, The Main Menu.
            mainVBox.getChildren().clear(); //Clear all the Children in VBox
            mainVBox.getChildren().add(mainMenu); //Add the main Menu back in the VBox, cleaned;
            mainVBox.getChildren().addAll(newVBox.getChildren()); //Add all the Children of the new VBox in the Main VBox.



        }catch (IOException e){
            Alerts.showAlert("IO Exception", "Error loading View", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}

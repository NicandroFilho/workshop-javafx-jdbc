package gui;

import application.Main;
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
import model.services.DepartmentService;
import model.services.SellerService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class MainViewController implements Initializable {
    @FXML
    private MenuItem menuItemSeller;
    @FXML
    private MenuItem menuItemDepartment;
    @FXML
    private MenuItem menuItemAbout;

    @FXML
    public void onMenuItemSellerAction(){
        loadView("/gui/SellerList.fxml", (SellerListController controller)-> {
            controller.setSellerService(new SellerService());
            controller.updateTableView();
        });
    }

    @FXML
    public void onMenuItemDepartmentAction(){
        //load the View of Department
        loadView("/gui/DepartmentList.fxml", (DepartmentListController controller)-> {
            controller.setDepartmentService(new DepartmentService());
            controller.updateTableView();
            });
    }

    @FXML
    public void onMenuItemAboutAction(){
        //load the View of About
        loadView("/gui/About.fxml", x -> {});
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO: 23/09/2019
    }

    private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction ){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            VBox newVBox = loader.load();

            Scene mainScene = Main.getMainScene();
            VBox mainVBox = (VBox) (((ScrollPane)mainScene.getRoot()).getContent()); //get the main VBox

            Node mainMenu = mainVBox.getChildren().get(0); //stores the firs children of main VBox. In this case, The Main Menu.
            mainVBox.getChildren().clear(); //Clear all the Children in VBox
            mainVBox.getChildren().add(mainMenu); //Add the main Menu back in the VBox, cleaned;
            mainVBox.getChildren().addAll(newVBox.getChildren()); //Add all the Children of the new VBox in the Main VBox.

            //Execução da Função Lambda passada como Argumento.
            T controller = loader.getController();
            initializingAction.accept(controller);

        }catch (IOException e){
            Alerts.showAlert("IO Exception", "Error loading View", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}

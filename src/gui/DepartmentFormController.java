package gui;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DepartmentFormController implements Initializable {

    /*Attributes*/
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private Button btSave;
    @FXML
    private Button btCancel;
    @FXML
    private Label labelErrorName;
    
    /*Methods*/
    @FXML
    public void onBtSaveAction(){
        // TODO: 24/09/2019
        System.out.println("onBtSaveAction");
    }
    
    @FXML
    public void onBtCancelAction(){
        // TODO: 24/09/2019
        System.out.println("onBtCancelAction");
    }

    private void initializeNodes(){
        Constraints.setTextFiledInteger(txtId);
        Constraints.setTextFieldMaxLength(txtName, 20);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }
}

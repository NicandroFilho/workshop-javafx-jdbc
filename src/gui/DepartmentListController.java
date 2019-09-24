package gui;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DepartmentListController implements Initializable {

    private DepartmentService service;
    private ObservableList<Department> obsList;

    @FXML
    private TableView<Department> tableViewDepartment;

    @FXML
    private TableColumn<Department, Integer> tableColumnId;

    @FXML
    private TableColumn<Department, String> tableColumnName;

    @FXML
    private Button btNew;


    public void setDepartmentService (DepartmentService service){
        this.service = service;
    }

    public void updateTableView(){
        if(service == null){
            throw new IllegalStateException("Service is Null!");
        }
        List<Department> list = service.findAll(); //Carrega os dados da Tabela de Department na Lista
        obsList = FXCollections.observableArrayList(list); // Coloca os dados da Lista no ObservableList
        tableViewDepartment.setItems(obsList); // Atualiza os dados do TableView com os dados do ObservableList
    }

    @FXML
    public void onBtNewAction(ActionEvent event){
        Stage parentStage = Utils.currentStage(event);
        createDialogForm("/gui/DepartmentForm.fxml", parentStage);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes() {
        //Mecanismo do JavaFX para iniciar o comportamento das colunas da TableView
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        //Mecanismo para o tableView acompanhar a altura da janela
        Stage stage = (Stage)Main.getMainScene().getWindow();
        tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
    }

    private void createDialogForm(String absoluteName, Stage parentStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Department Data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL); //Janela bloqueia janelas anteriores
            dialogStage.showAndWait();

        }catch (IOException e){
            Alerts.showAlert("IOException", "Error Loading View", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}

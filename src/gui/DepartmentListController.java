package gui;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DepartmentListController implements Initializable, DataChangeListener {

    public Button btNew;
    private DepartmentService service;


    @FXML
    private TableView<Department> tableViewDepartment;

    @FXML
    private TableColumn<Department, Integer> tableColumnId;

    @FXML
    private TableColumn<Department, String> tableColumnName;

    @FXML
    private TableColumn<Department,Department> tableColumnEDIT;

    @FXML
    private TableColumn<Department,Department> tableColumnREMOVE;


    void setDepartmentService(DepartmentService service){
        this.service = service;
    }

    void updateTableView(){
        if(service == null){
            throw new IllegalStateException("Service is Null!");
        }
        List<Department> list = service.findAll(); //Carrega os dados da Tabela de Department na Lista
        ObservableList<Department> obsList = FXCollections.observableArrayList(list); // Coloca os dados da Lista no ObservableList
        tableViewDepartment.setItems(obsList); // Atualiza os dados do TableView com os dados do ObservableList
        initEditButtons(); //update the edit buttons column to show the buttons on the existent rows
        initRemoveButtons();
    }

    @FXML
    public void onBtNewAction(ActionEvent event){
        Stage parentStage = Utils.currentStage(event);
        Department obj = new Department();
        createDialogForm(obj, parentStage);
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

    private void initEditButtons(){
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("edit");

            @Override
            protected void updateItem(Department obj, boolean empty) {
                super.updateItem(obj, empty);

                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(obj, Utils.currentStage(event)));
            }
        });
    }

    private void initRemoveButtons(){
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("remove");

            @Override
            protected void updateItem(Department obj, boolean empty) {
                super.updateItem(obj, empty);

                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeEntity(obj));
            }
        });
    }

    private void removeEntity(Department obj) {
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure you want to DELET this Department?");
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                if (service == null) {
                    throw new IllegalStateException("Service is null");
                }
                try {
                    service.remove(obj);
                    updateTableView();
                } catch (DbIntegrityException e) {
                    Alerts.showAlert("Error removing object from Data Base", null, e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }
    }

    private void createDialogForm(Department obj, Stage parentStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/DepartmentForm.fxml"));
            Pane pane = loader.load();

            DepartmentFormController controller = loader.getController();

            controller.setDepartment(obj);
            controller.setDepartmentService(new DepartmentService());
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Department Data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL); //Janela bloqueia janelas anteriores
            dialogStage.showAndWait();

        }catch (IOException e){
            e.printStackTrace();
            Alerts.showAlert("IOException", "Error Loading View", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
    public void onDataChanged() {
        updateTableView();
    }
}

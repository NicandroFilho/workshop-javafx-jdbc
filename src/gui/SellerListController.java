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
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SellerListController implements Initializable, DataChangeListener {

    public Button btNew;
    private SellerService service;


    @FXML
    private TableView<Seller> tableViewSeller;

    @FXML
    private TableColumn<Seller, Integer> tableColumnId;

    @FXML
    private TableColumn<Seller, String> tableColumnName;

    @FXML
    private TableColumn<Seller, String> tableColumnEmail;

    @FXML
    private TableColumn<Seller, Date> tableColumnBirthDate;

    @FXML
    private TableColumn<Seller, Double> tableColumnBaseSalary;

    @FXML
    private TableColumn<Seller,Seller> tableColumnEDIT;

    @FXML
    private TableColumn<Seller,Seller> tableColumnREMOVE;


    void setSellerService(SellerService service){
        this.service = service;
    }

    void updateTableView(){
        if(service == null){
            throw new IllegalStateException("Service is Null!");
        }
        List<Seller> list = service.findAll(); //Carrega os dados da Tabela de Seller na Lista
        ObservableList<Seller> obsList = FXCollections.observableArrayList(list); // Coloca os dados da Lista no ObservableList
        tableViewSeller.setItems(obsList); // Atualiza os dados do TableView com os dados do ObservableList
        initEditButtons(); //update the edit buttons column to show the buttons on the existent rows
        initRemoveButtons();
    }

    @FXML
    public void onBtNewAction(ActionEvent event){
        Stage parentStage = Utils.currentStage(event);
        Seller obj = new Seller();
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
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
        tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);

        //Mecanismo para o tableView acompanhar a altura da janela
        Stage stage = (Stage)Main.getMainScene().getWindow();
        tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
    }

    private void initEditButtons(){
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("edit");

            @Override
            protected void updateItem(Seller obj, boolean empty){
                super.updateItem(obj,empty);

                if(obj == null){
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
            protected void updateItem(Seller obj, boolean empty) {
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

    private void removeEntity(Seller obj) {
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure you want to DELET this Seller?");
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

    private void createDialogForm(Seller obj, Stage parentStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SellerForm.fxml"));
            Pane pane = loader.load();

            SellerFormController controller = loader.getController();

            controller.setSeller(obj);
            controller.setSellerServices(new SellerService(), new DepartmentService());
            controller.loadAssociatedObjects();
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Seller Data");
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

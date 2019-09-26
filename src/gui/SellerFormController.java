package gui;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class SellerFormController implements Initializable {

    /*Dependencies*/
    private Seller entity;
    private SellerService service;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    /*Form Attributes*/

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtEmail;
    @FXML
    private DatePicker dpBirthDate;
    @FXML
    private TextField txtBaseSalary;
    @FXML
    private Button btSave;
    @FXML
    private Button btCancel;
    @FXML
    private Label labelErrorName;
    @FXML
    private Label labelErrorEmail;
    @FXML
    private Label labelErrorBirthDate;
    @FXML
    private Label labelErrorBaseSalary;
    
    /*Methods*/
    @FXML
    public void onBtSaveAction(ActionEvent event){
        if (entity == null){
            throw new IllegalStateException("Entity is null");
        }
        if(service == null){
            throw new IllegalStateException(("Service is null"));
        }
        try {
            entity = getFormData();
            service.saveOrUpdate(entity);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        }catch (ValidationException e) {
            setErrorMessages(e.getErrors());
        }catch (DbException e){
            Alerts.showAlert("Error Saving object", null,e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void notifyDataChangeListeners() {
        for(DataChangeListener listener: dataChangeListeners){
            listener.onDataChanged();
        }
    }

    private Seller getFormData() {

        Seller obj = new Seller();
        ValidationException exception = new ValidationException("Validation Error");
        obj.setId(Utils.tryParseToInt(txtId.getText()));

        if(txtName.getText() == null || txtName.getText().trim().equals("")) {
            exception.addError("Name", "Field can't be empty.");
        }
        obj.setName(txtName.getText());

        if(exception.getErrors().size() > 0){
            throw exception;
        }
        return obj;
    }

    @FXML
    public void onBtCancelAction(ActionEvent event){
        Utils.currentStage(event).close();
    }

    private void initializeNodes(){
        Constraints.setTextFiledInteger(txtId);
        Constraints.setTextFieldMaxLength(txtName, 40);
        Constraints.setTextFiledDouble(txtBaseSalary);
        Constraints.setTextFieldMaxLength(txtEmail, 30);
        Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    public void setSeller(Seller entity){
        this.entity = entity;
    }

    public void setSellerService(SellerService service){
        this.service = service;
    }

    public void updateFormData(){
        if(entity == null){
            throw new IllegalStateException("The entity is null");
        }
        txtId.setText(String.valueOf(entity.getId()));
        txtName.setText(entity.getName());
        txtEmail.setText(entity.getEmail());
        txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
        if(entity.getBirthDate() != null) {
            dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
        }
    }

    public void subscribeDataChangeListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }

    private void setErrorMessages(Map<String, String> errors){
        Set<String> fields = errors.keySet();
        if(fields.contains("Name")){
            labelErrorName.setText(errors.get("Name"));
        }
    }
}

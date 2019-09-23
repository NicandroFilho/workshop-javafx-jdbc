package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

    /*
    * Acesso ao Stage aonde o controler que recebeu o evento está inserido.
    * Por exemplo, se eu clico num botão, eu vou pegar o Stage daquele botão
    * */
    public static Stage currentStage(ActionEvent event){
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }
}

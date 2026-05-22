package mapademo;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import upv.ipc.sportlib.AnnotationType;

public class AnotacionesDialogController implements Initializable {

    @FXML
    private TextField textoTextField;
    @FXML
    private ComboBox<String> tipoComboBox;

    private boolean guardado = false;
    private String textoFinal;
    private String tipoFinal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void guardar(ActionEvent event) {
        textoFinal = textoTextField.getText();
        tipoFinal = tipoComboBox.getValue();
        guardado = true;
        cerrarVentana(event);
    }

    @FXML
    private void cancelar(ActionEvent event) {
        guardado = false;
        cerrarVentana(event);
    }

    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    public boolean isGuardado() {
        return guardado;
    }

    public String getTextoFinal() {
        return textoFinal;
    }

    public upv.ipc.sportlib.AnnotationType getTipoFinalEnum() {
        if (tipoFinal == null) return AnnotationType.POINT;
        
        switch (tipoFinal) {
            case "Punto":
                return AnnotationType.POINT;
            case "Texto Libre":
                return AnnotationType.TEXT;
            case "Línea":
                return AnnotationType.LINE;
            case "Círculo":
                return AnnotationType.CIRCLE;
            default:
                return AnnotationType.POINT;
        }
    }
}

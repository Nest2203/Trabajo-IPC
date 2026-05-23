package mapademo;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;

public class NuevoMapaController implements Initializable {

    @FXML
    private TextField nombreTextField;
    @FXML
    private TextField latMinTextField;
    @FXML
    private TextField latMaxTextField;
    @FXML
    private TextField lonMinTextField;
    @FXML
    private TextField lonMaxTextField;
    @FXML
    private Label imagenRutaLabel;
    @FXML
    private Button seleccionarImagenBtn;
    @FXML
    private Button guardarBtn;

    private File img;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void seleccionarImagen(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar Imagen del Mapa");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagenes", "*.png", "*.jpg", "*.jpeg"));
        
        Stage s = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File f = fc.showOpenDialog(s);
        
        if (f != null) {
            img = f;
            imagenRutaLabel.setText(f.getName());
        }
    }

    @FXML
    private void guardarMapa(ActionEvent event) {
        String nom = nombreTextField.getText();
        String lMinStr = latMinTextField.getText();
        String lMaxStr = latMaxTextField.getText();
        String loMinStr = lonMinTextField.getText();
        String loMaxStr = lonMaxTextField.getText();

        if (nom.isEmpty() || lMinStr.isEmpty() || lMaxStr.isEmpty() || loMinStr.isEmpty() || loMaxStr.isEmpty() || img == null) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(null);
            a.setContentText("Faltan datos por rellenar.");
            a.showAndWait();
            return;
        }

        try {
            double lMin = Double.parseDouble(lMinStr);
            double lMax = Double.parseDouble(lMaxStr);
            double loMin = Double.parseDouble(loMinStr);
            double loMax = Double.parseDouble(loMaxStr);

            if (lMin > lMax || loMin > loMax) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText(null);
                a.setContentText("Las coordenadas minimas no pueden ser mayores que las maximas.");
                a.showAndWait();
                return;
            }

           
            if (lMin < -90 || lMax > 90) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Rango inválido");
                a.setHeaderText(null);
                a.setContentText("La latitud debe ser un número entre -90 y 90.");
                a.showAndWait();
                return;
            }
            
            if (loMin < -180 || loMax > 180) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Rango inválido");
                a.setHeaderText(null);
                a.setContentText("La longitud debe ser un número entre -180 y 180.");
                a.showAndWait();
                return;
            }

            SportActivityApp app = SportActivityApp.getInstance();
            app.addMapRegion(nom, img, lMin, lMax, loMin, loMax);

            Alert a2 = new Alert(Alert.AlertType.INFORMATION);
            a2.setTitle("Ok");
            a2.setHeaderText(null);
            a2.setContentText("El mapa se ha guardado.");
            a2.showAndWait();
            
            Stage s = (Stage) ((Node) event.getSource()).getScene().getWindow();
            s.close();

        } catch (NumberFormatException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(null);
            a.setContentText("Las coordenadas tienen que ser numeros.");
            a.showAndWait();
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(null);
            a.setContentText("Error al guardar: " + e.getMessage());
            a.showAndWait();
        }
    }

    @FXML
    private void cancelar(ActionEvent event) {
        Stage s = (Stage) ((Node) event.getSource()).getScene().getWindow();
        s.close();
    }
}

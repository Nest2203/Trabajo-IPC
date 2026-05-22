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

    private File imagenSeleccionada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void seleccionarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen del Mapa");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes (*.png, *.jpg)", "*.png", "*.jpg", "*.jpeg")
        );
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            imagenSeleccionada = file;
            imagenRutaLabel.setText(file.getName());
        }
    }

    @FXML
    private void guardarMapa(ActionEvent event) {
        String nombre = nombreTextField.getText();
        String latMinStr = latMinTextField.getText();
        String latMaxStr = latMaxTextField.getText();
        String lonMinStr = lonMinTextField.getText();
        String lonMaxStr = lonMaxTextField.getText();

        if (nombre.isEmpty() || latMinStr.isEmpty() || latMaxStr.isEmpty() || 
            lonMinStr.isEmpty() || lonMaxStr.isEmpty() || imagenSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campos incompletos", "Por favor, completa todos los campos y selecciona una imagen.");
            return;
        }

        try {
            double latMin = Double.parseDouble(latMinStr);
            double latMax = Double.parseDouble(latMaxStr);
            double lonMin = Double.parseDouble(lonMinStr);
            double lonMax = Double.parseDouble(lonMaxStr);

            if (latMin > latMax || lonMin > lonMax) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Coordenadas", "La latitud/longitud mínima no puede ser mayor que la máxima.");
                return;
            }

            SportActivityApp app = SportActivityApp.getInstance();
            app.addMapRegion(nombre, imagenSeleccionada, latMin, latMax, lonMin, lonMax);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Mapa guardado correctamente.");
            cerrarVentana(event);

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "Las coordenadas deben ser números válidos.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar", "Ocurrió un error al guardar el mapa: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}

package mapademo;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import model.SessionActivity;

public class ActivitiesViewController implements Initializable {

    @FXML
    private TableView<SessionActivity> tabla;
    @FXML
    private TableColumn<SessionActivity, String> columaNombre;
    @FXML
    private TableColumn<SessionActivity, Double> columnaDistancia;
    @FXML
    private Label DistanciaTtotal;
    @FXML
    private Label TiempoTotal;

    private ObservableList<SessionActivity> listaActividades;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaActividades = FXCollections.observableArrayList();
        columaNombre.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        columnaDistancia.setCellValueFactory(cellData -> cellData.getValue().distanceProperty().asObject());
        
        tabla.setItems(listaActividades);
        actualizarEstadisticas();
    }
    
    @FXML
    private void handleImportar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo GPX");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos GPX (*.gpx)", "*.gpx")
        );
        File selectedFile = fileChooser.showOpenDialog(tabla.getScene().getWindow());
        if (selectedFile != null) {
            try {
                String nombre = selectedFile.getName().replace(".gpx", "");
                SessionActivity nuevaActividad = new SessionActivity(nombre, LocalDateTime.now(), 10.5, 3240, 120.0);
                listaActividades.add(nuevaActividad);
                actualizarEstadisticas();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo cargar el archivo", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleRenombrar(ActionEvent event) {
        SessionActivity seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Atención", "Selecciona una actividad primero", Alert.AlertType.WARNING);
            return;
        }
        TextInputDialog dialog = new TextInputDialog(seleccionada.getName());
        dialog.setTitle("Renombrar");
        dialog.setHeaderText("Modificar nombre");
        dialog.setContentText("Nuevo nombre:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            seleccionada.setName(result.get().trim());
        }
    }

    @FXML
    private void handleBorrar(ActionEvent event) {
        SessionActivity seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Atención", "Selecciona una actividad primero", Alert.AlertType.WARNING);
            return;
        }
        listaActividades.remove(seleccionada);
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        double kmTotales = 0.0;
        long segTotales = 0;
        for (SessionActivity act : listaActividades) {
            kmTotales += act.getDistance();
            segTotales += act.getDuration();
        }
        long horas = segTotales / 3600;
        long minutos = (segTotales % 3600) / 60;
        
        DistanciaTtotal.setText(String.format("Distancia Total Acumulada: %.2f km", kmTotales));
        TiempoTotal.setText(String.format("Tiempo Total de Carrera: %02dh %02dmin", horas, minutos));
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
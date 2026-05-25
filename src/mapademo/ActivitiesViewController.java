package mapademo;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import model.SessionActivity;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.SportActivityApp;

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
    private HBox miBarraError;

    private ObservableList<SessionActivity> listaActividades;
    @FXML
    private Button verActividad;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        listaActividades = FXCollections.observableArrayList();
        
        columaNombre.setCellValueFactory(data -> data.getValue().nameProperty());
        columnaDistancia.setCellValueFactory(data -> data.getValue().distanceProperty().asObject());
        
        tabla.setItems(listaActividades);
        
        actualizarEstadisticas();
        
        if (miBarraError != null) {
            miBarraError.setVisible(false);
        }
        
        tabla.getSelectionModel().selectedItemProperty().addListener((o, viejo, nuevo) -> {
            if (nuevo != null) {
                handleActividadSeleccionada(nuevo);
            }
        });
    }
    
    @FXML
    private void handleImportar(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar archivo GPX");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos GPX", "*.gpx"));
        
        File f = fc.showOpenDialog(tabla.getScene().getWindow());
        if (f != null) {
            try {
                SessionActivity act = model.GPX.parse(f);
                SportActivityApp.getInstance().importActivity(f);
                listaActividades.add(act);
                actualizarEstadisticas();
                if (miBarraError != null) {
                    miBarraError.setVisible(false);
                }
            } catch (Exception e) {
                if (miBarraError != null) {
                    miBarraError.setVisible(true);
                } else {
                    Alert err = new Alert(AlertType.ERROR);
                    err.setTitle("Error");
                    err.setHeaderText(null);
                    err.setContentText("No se pudo cargar el archivo");
                    err.showAndWait();
                }
            }
        }
    }

    @FXML
    private void handleRenombrar(ActionEvent event) {
        SessionActivity sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Alert warn = new Alert(AlertType.WARNING);
            warn.setTitle("Atención");
            warn.setHeaderText(null);
            warn.setContentText("Selecciona una actividad primero");
            warn.showAndWait();
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog(sel.getName());
        dialog.setTitle("Renombrar");
        dialog.setHeaderText("Modificar nombre");
        dialog.setContentText("Nuevo nombre:");
        
        Optional<String> res = dialog.showAndWait();
        if (res.isPresent() && !res.get().trim().isEmpty()) {
            sel.setName(res.get().trim());
        }
    }

    @FXML
    private void handleBorrar(ActionEvent event) {
        SessionActivity sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Alert warn = new Alert(AlertType.WARNING);
            warn.setTitle("Atención");
            warn.setHeaderText(null);
            warn.setContentText("Selecciona una actividad primero");
            warn.showAndWait();
            return;
        }
        listaActividades.remove(sel);
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        double kms = 0.0;
        long segs = 0;
        
        if (listaActividades != null) {
            for (SessionActivity a : listaActividades) {
                kms += a.getDistance();
                segs += a.getDuration();
            }
        }
        
        long hrs = segs / 3600;
        long mins = (segs % 3600) / 60;
        
        DistanciaTtotal.setText("Distancia Total Acumulada: " + String.format("%.2f", kms) + " km");
        TiempoTotal.setText("Tiempo Total de Carrera: " + String.format("%02d", hrs) + "h " + String.format("%02d", mins) + "min");
    }
    
    private void handleActividadSeleccionada(SessionActivity actividad) {
        System.out.println("Actividad seleccionada en la tabla: " + actividad.getName());
    }
    
    @FXML
    private void abrirMapa(ActionEvent event) {
        
        SessionActivity actividadSeleccionada = tabla.getSelectionModel().getSelectedItem();
       
        if (actividadSeleccionada == null) {
            System.out.println("Por favor, selecciona una actividad primero.");
            return; 
        }
        

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VistaRuta.fxml"));
            Parent tuVista = loader.load();
            VistaRutaController tuControlador = loader.getController();
            
            
            Activity actividadOficial = null;
            for (Activity a : SportActivityApp.getInstance().getUserActivities()) {
                if (a.getName().equals(actividadSeleccionada.getName())) {
                    actividadOficial = a;
                    break;
                }
            }

            
            if (actividadOficial == null && !SportActivityApp.getInstance().getUserActivities().isEmpty()) {
                int ultima = SportActivityApp.getInstance().getUserActivities().size() - 1;
                actividadOficial = SportActivityApp.getInstance().getUserActivities().get(ultima);
            }

            
            if (actividadOficial != null) {
                tuControlador.mostrarActividadEnMapa(actividadOficial);
            } else {
                System.out.println(" ERROR: La base de datos de actividades está completamente vacía.");
            }

            tuControlador.setEscenaAnterior(tabla.getScene());
            javafx.stage.Stage stage = (javafx.stage.Stage) tabla.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(tuVista));
            stage.show();

        } catch (java.io.IOException e) {
            System.out.println("Error al cargar la pestaña de la ruta: " + e.getMessage());
            e.printStackTrace();
        }
}
}

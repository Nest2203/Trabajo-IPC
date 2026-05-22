package mapademo;

import java.net.URL;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import upv.ipc.sportlib.Session;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

public class HistorialSesionesController implements Initializable {

    @FXML
    private ListView<Session> sesionesListView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SportActivityApp app = SportActivityApp.getInstance();
        User currentUser = app.getCurrentUser();
        
        if (currentUser != null) {
            List<Session> sesiones = app.getSessionsByUser(currentUser);
            
            // Ordenar de la más reciente a la más antigua con protección contra nulls
            sesiones.sort((s1, s2) -> {
                if (s1.getStartTime() == null && s2.getStartTime() == null) return 0;
                if (s1.getStartTime() == null) return 1;
                if (s2.getStartTime() == null) return -1;
                return s2.getStartTime().compareTo(s1.getStartTime());
            });
            
            ObservableList<Session> observableSesiones = FXCollections.observableArrayList(sesiones);
            sesionesListView.setItems(observableSesiones);
            
            // CellFactory para mostrar la info solicitada con el formato correcto
            sesionesListView.setCellFactory(param -> new ListCell<Session>() {
                @Override
                protected void updateItem(Session item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        // Formatear Fecha de Inicio
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        String fechaInicio = item.getStartTime() != null 
                            ? item.getStartTime().format(formatter) 
                            : "Fecha desconocida";

                        // Formatear Duración
                        String duracionFormateada = "00:00:00";
                        Duration duracion = item.getDuration();
                        if (duracion != null) {
                            long totalSegundos = duracion.getSeconds();
                            long horas = totalSegundos / 3600;
                            long minutos = (totalSegundos % 3600) / 60;
                            long segundos = totalSegundos % 60;
                            duracionFormateada = String.format("%02d:%02d:%02d", horas, minutos, segundos);
                        }
                        
                        String texto = String.format(
                            "Inicio: %s | Duración: %s\n" +
                            "Actividades Importadas: %d | Actividades Abiertas: %d\n" +
                            "Anotaciones Creadas: %d",
                            fechaInicio,
                            duracionFormateada, 
                            item.getImportedActivities(),
                            item.getViewedActivities(),
                            item.getAnnotationsCreated()
                        );
                        setText(texto);
                    }
                }
            });
        }
    }    

    @FXML
    private void cerrar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}

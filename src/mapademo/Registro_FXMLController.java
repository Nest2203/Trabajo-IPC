/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

/**
 * FXML Controller class
 *
 * @author thoma
 */
public class Registro_FXMLController implements Initializable {

    @FXML
    private PasswordField textoPassword;
    @FXML
    private TextField textoNickname;
    @FXML
    private TextField textoEmail;
    @FXML
    private DatePicker textoFecha;
    @FXML
    private Label textoError;
    @FXML
    private Button botonRegistrar;
    @FXML
    private Button buttonVolver;
    @FXML
    private ImageView avatarPreview;
    @FXML
    private Button botonAvatar;
    
    private String rutaAvatar = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textoError.setText("");
    }    

    @FXML
    private void pulsaRegistrar(ActionEvent event) {
        String nick = textoNickname.getText();
        String email = textoEmail.getText();
        String pass = textoPassword.getText();
        LocalDate fecha = textoFecha.getValue();
        
        if(nick == null || nick.isEmpty() || email == null || email.isEmpty() ||
                pass == null || pass.isEmpty() || fecha == null) {
            
            textoError.setText("Por favor, rellena todos los campos.");
            textoError.setStyle("fx-text-fill: red;");
            return;
        }
        if (!User.checkNickName(nick)) {
            textoError.setText("Nickname inválido (6-15 caracteres: letras, números o guiones).");
            textoError.setStyle("-fx-text-fill: red;");
            return;
        }
        if (!User.checkEmail(email)) {
            textoError.setText("El formato del correo electrónico no es válido");
            textoError.setStyle("-fx-text-fill: red;");
            return;
        }
        if (!User.checkPassword(pass)) {
            textoError.setText("La contraseña debe tener 8-20 caracteres, mayúsculas, minúsculas, números y símbolos");
            textoError.setStyle("-fx-text-fill: red;");
            return;
        }
        if (!User.isOlderThan(fecha, 12)) {
            textoError.setText("Debes tener más de 12 años para poder registrarte.");
            textoError.setStyle("-fx-text-fill: red;");
            return;
        }
        SportActivityApp app = SportActivityApp.getInstance();
        boolean registrado = app.registerUser(nick, email, pass, fecha, rutaAvatar);
        
        if (registrado) {
            textoError.setText("¡Usuario registrado con éxito! Espera...");
            textoError.setStyle("-fx-text-fill: green;");
           
            PauseTransition espera = new PauseTransition(Duration.seconds(3));
            espera.setOnFinished(e -> {
           try {
               FXMLLoader loader = new FXMLLoader(getClass().getResource("Login_FXML.fxml"));
               Parent root = loader.load();
               Stage stage = (Stage) textoError.getScene().getWindow();
               stage.setScene(new Scene(root));
               stage.show();
           }
           catch (IOException ex) {
               ex.printStackTrace();
           }
            
        });
            
        espera.play();
        }    
        else {
            textoError.setText("Error. Es posible que el nickname ya esté en uso.");
            textoError.setStyle("-fx-text-fill: red;");
            
            
        }
    }

    @FXML
    private void pulsaVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login_FXML.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) buttonVolver.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Iniciar Sesión");
            stage.show();
        }
        catch (Exception e) {
            System.out.println("Error al volver a la pantalla de Login: " + e.getMessage());
        }
    }

    @FXML
    private void pulsaAvatar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen de Perfil");
        fileChooser.getExtensionFilters().addAll(new javafx.stage.FileChooser.ExtensionFilter("Imágenes", "*png", "*jpg", "*jpeg"));
        
        java.io.File archivoSeleccionado = fileChooser.showOpenDialog(botonAvatar.getScene().getWindow());
        if (archivoSeleccionado != null) {
            rutaAvatar = archivoSeleccionado.getAbsolutePath();
            Image imagen = new Image(archivoSeleccionado.toURI().toString());
            avatarPreview.setImage(imagen);
        }
    }
    
}

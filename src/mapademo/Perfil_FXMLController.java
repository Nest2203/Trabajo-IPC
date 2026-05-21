/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

/**
 * FXML Controller class
 *
 * @author thoma
 */
public class Perfil_FXMLController implements Initializable {

    @FXML
    private ImageView avatarPreview;
    @FXML
    private Button botonCambiarAvatar;
    @FXML
    private TextField textoNickname;
    @FXML
    private TextField textoEmail;
    @FXML
    private PasswordField textoPassword;
    @FXML
    private DatePicker textoFecha;
    @FXML
    private Button botonGuardar;
    @FXML
    private Label textoError;
    
    private String rutaAvatarActual = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textoError.setText("");
        
        SportActivityApp app = SportActivityApp.getInstance();
        User usuario = app.getCurrentUser();
        
        if (usuario != null) {
            textoNickname.setText(usuario.getNickName());
            textoEmail.setText(usuario.getEmail());
            textoPassword.setText(usuario.getPassword());
            textoFecha.setValue(usuario.getBirthDate());
            rutaAvatarActual = usuario.getAvatarPath();
            
            if (rutaAvatarActual != null && !rutaAvatarActual.isEmpty()) {
                File archivoFoto = new File(rutaAvatarActual);
                if (archivoFoto.exists()) {
                    avatarPreview.setImage(new Image(archivoFoto.toURI().toString()));
                }
            }
        }
    }    

    @FXML
    private void pulsaCambiarAvatar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen de Perfil");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File archivoSeleccionado = fileChooser.showOpenDialog(botonCambiarAvatar.getScene().getWindow());
        
        if (archivoSeleccionado != null) {
            rutaAvatarActual = archivoSeleccionado.getAbsolutePath();
            avatarPreview.setImage(new Image(archivoSeleccionado.toURI().toString()));
        }
        
    }

    @FXML
    private void pulsaGuardar(ActionEvent event) {
        SportActivityApp app = SportActivityApp.getInstance();
        User usuario = app.getCurrentUser();
        
        if (usuario != null) {
            String nuevoEmail = textoEmail.getText();
            String nuevaPass = textoPassword.getText();
            LocalDate nuevaFecha = textoFecha.getValue();
            
            if (nuevoEmail.isEmpty() || nuevaPass.isEmpty() || nuevaFecha == null) {
                textoError.setText("Por favor, rellena todos los campos editables");
                textoError.setStyle("-fx-text-fill: red;");
                return;
            }
            if (!User.checkEmail(nuevoEmail)) {
                textoError.setText("El formato del correo electrónico no es válido.");
                textoError.setStyle("-fx-text-fill: red;");
                return;
            }
            if (!User.checkPassword(nuevaPass)) {
                textoError.setText("La contraseña debe tener 8-20 caracteres, mayúsculas, minúsculas, números y signos");
                textoError.setStyle("-fx-text-fill: red;");
                return;
            }
            if (!User.isOlderThan(nuevaFecha, 12)) {
                textoError.setText("Debes tener más de 12 años");
                textoError.setStyle("-fx-text-fill: red;");
                return;
            }
            
            usuario.setEmail(nuevoEmail);
            usuario.setPassword(nuevaPass);
            usuario.setBirthDate(nuevaFecha);
            usuario.setAvatarPath(rutaAvatarActual);
            app.saveUser(usuario);
            
            textoError.setText("¡Cambios guardados con éxito!");
            textoError.setStyle("-fx-text-fill: green;");
        }
    }

    @FXML
    private void pulsaNickname(MouseEvent event) {
        textoError.setText("El nombre de usuario no se puede modificar.");
        textoError.setStyle("-fx-text-fill: red;");
    }
    
}

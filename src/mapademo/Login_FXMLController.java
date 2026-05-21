/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;

/**
 * FXML Controller class
 *
 * @author thoma
 */
public class Login_FXMLController implements Initializable {

    @FXML
    private TextField textoNickname;
    @FXML
    private PasswordField textoPassword;
    @FXML
    private Label textoError;
    @FXML
    private Button botonLogin;
    @FXML
    private Hyperlink linkRegistro;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void pulsaRegistrarse(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Registro_FXML.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Registro de Usuario");
            stage.show();
        }
        catch (IOException e) {
            System.out.println("Error al abrir la pantalla de registro: " + e.getMessage());
        }
    }

    @FXML
    private void pulsaLogin(ActionEvent event) {
        String nick = textoNickname.getText();
        String pass = textoPassword.getText();

        if (nick.isEmpty() || pass.isEmpty()) {
            textoError.setText("Por favor, rellena todos los campos.");
            return;
        }

        //Llamada a la librería
        SportActivityApp app = SportActivityApp.getInstance();
        boolean loginCorrecto = app.login(nick, pass);
        if (loginCorrecto) {
            textoError.setText("¡Login correcto!");
            textoError.setStyle("-fx-text-fill: green;");
            
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) textoNickname.getScene().getWindow();
                stage.setTitle("Running La Safor - Mapa");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                System.out.println("Error al cargar la pantalla del mapa: " + e.getMessage());
            }
            
        } else {
            textoError.setText("Usuario o contraseña incorrectos.");
            textoError.setStyle("-fx-text-fill: red;");
        }
    }
}
    

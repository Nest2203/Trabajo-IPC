/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mapademo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import model.SessionActivity;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.MapProjection;
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.TrackPoint;


/**
 * FXML Controller class
 *
 * @author saulnolla
 */
public class VistaRutaController implements Initializable {

    @FXML
    private Slider zoom_slider;
    @FXML
    private AreaChart<Number,Number> graficaDesnivel;
    @FXML
    private Label lblCordenadas;
    @FXML
    private SplitPane splitPane;
    @FXML
    private ListView<Poi> map_listview;
    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private ImageView imagenMapa;
    @FXML
    private Label lblDistancia;
    @FXML
    private Label lblTiempo;
    @FXML
    private Label lblRitmo;
    @FXML
    private Label lblVelocidad;
    @FXML
    private Label lblDesnivelpos;
    @FXML
    private Label lblDesnivelneg;
    @FXML
    private Label lblDenivelpos;
    
    private Group zoomGroup;
    private Pane mapPane;
    private ContextMenu mapContextMenu;
    private boolean insertionMode = false;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        zoom_slider.setMin(0.5);
        zoom_slider.setMax(1.5);
        zoom_slider.setValue(1.0);
        zoom_slider.valueProperty().addListener((observable, oldVal, newVal) -> {
            zoom(newVal.doubleValue());                
    });
        MenuItem miText   = new MenuItem("📝 Añadir texto");
        MenuItem miCircle = new MenuItem("⭕ Añadir círculo");
        mapContextMenu = new ContextMenu(miText, miCircle);
        if (map_listview != null) {
            map_listview.setCellFactory(listView -> new ListCell<Poi>() {
                @Override
                protected void updateItem(Poi poi, boolean empty) {
                    super.updateItem(poi, empty);
                    if (empty || poi == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(poi.getCode() + " – " + poi.getPosition());
                    }
                }
            });
        }
    }
        public void mostrarActividadEnMapa(Activity activity) {
            if (activity == null) return;
        
         try{           
           if (lblDistancia != null) lblDistancia.setText(String.format("%.2f km", activity.getTotalDistance() / 1000.0));
           if (lblTiempo != null) lblTiempo.setText(activity.getDuration().toMinutes() + " min");
           if (lblDesnivelpos != null) lblDesnivelpos.setText("+" + String.format("%.0f m", activity.getElevationGain()));
           MapRegion region = activity.getSuggestedMap();
           buildMap(new File(region.getImagePath()));
           MapProjection proj = new MapProjection(region, mapPane.getPrefWidth(), mapPane.getPrefHeight());
           Polyline route = new Polyline();
           route.setStroke(Color.BLUE);
           route.setStrokeWidth(3);
           XYChart.Series<Number, Number> perfilElevacion = new XYChart.Series<>();
           perfilElevacion.setName(activity.getName());
           double distanciaAcumulada = 0;
           TrackPoint prevPoint = null;
           Point2D prevP = null;
           for (TrackPoint tp : activity.getTrackPoints()) {
                    Point2D p = proj.project(tp);

                    if (prevPoint != null && prevP != null) {
                        double velocidad = tp.speedTo(prevPoint); 
                        javafx.scene.shape.Line segmento = new javafx.scene.shape.Line(prevP.getX(), prevP.getY(), p.getX(), p.getY());
                        segmento.setStrokeWidth(4);
                        if (velocidad < 8.0) {
                            segmento.setStroke(Color.RED);       
                        } else if (velocidad < 12.0) {
                            segmento.setStroke(Color.ORANGE);    
                        } else {
                            segmento.setStroke(Color.GREEN);     
                        }
                        mapPane.getChildren().add(segmento);

                        
                        distanciaAcumulada += prevPoint.distanceTo(tp) / 1000.0;
                    }
                    
                    
                    perfilElevacion.getData().add(new XYChart.Data<>(distanciaAcumulada, tp.getElevation()));
                   
                    prevPoint = tp;
                    prevP = p;
                }
           mapPane.getChildren().add(route);
           if (graficaDesnivel != null) {
               graficaDesnivel.getData().add(perfilElevacion);
           }
                
           if (map_listview != null) {
                    ObservableList<Poi> listaPuntos = FXCollections.observableArrayList();
                    
                    Point2D pInicio = proj.project(activity.getStartPoint());
                    Point2D pFin = proj.project(activity.getEndPoint());
                    listaPuntos.add(new Poi("Inicio de ruta", pInicio.getX(), pInicio.getY()));
                    listaPuntos.add(new Poi("Fin de ruta", pFin.getX(), pFin.getY()));
                    map_listview.setItems(listaPuntos);
}
           
           } catch (Exception e) {
            System.out.println("No se encontró el archivo 'ruta.gpx' en la raíz del proyecto. Cargando demo.");
            buildMap(new File("maps/upv.jpg"));
        }
        
    }
        
        
        
        
    
    
    
    

    @FXML
    void zoomOut(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal - 0.1);
    }

    @FXML
    void zoomIn(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal + 0.1);
    }


    @FXML
    private void showPosition(MouseEvent event) {
        if (lblCordenadas != null) {
            lblCordenadas.setText("X: " + (int) event.getX() + ", Y: " + (int) event.getY());
    }
    }
    private void zoom(double scaleValue) {
        // Guardamos la posición del scroll antes de escalar
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();

        // Aplicamos el zoom escalando el Group en ambos ejes
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);

        // Restauramos la posición del scroll para que el centro visual
        // permanezca estable durante el zoom
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }
    
    @FXML
    void listClicked(MouseEvent event) {
        
        Poi itemSelected = map_listview.getSelectionModel().getSelectedItem();
        if (itemSelected == null) return;

        
        double mapWidth  = mapPane.getWidth()  * zoomGroup.getScaleX();
        double mapHeight = mapPane.getHeight() * zoomGroup.getScaleY();

        
        double poiX = itemSelected.getPosition().getX() * zoomGroup.getScaleX();
        double poiY = itemSelected.getPosition().getY() * zoomGroup.getScaleY();

        
        double viewW = map_scrollpane.getViewportBounds().getWidth();
        double viewH = map_scrollpane.getViewportBounds().getHeight();

        
        double scrollH = (poiX - viewW / 2) / (mapWidth  - viewW);
        double scrollV = (poiY - viewH / 2) / (mapHeight - viewH);

        
        scrollH = Math.max(0, Math.min(1, scrollH));
        scrollV = Math.max(0, Math.min(1, scrollV));

        
        final Timeline timeline = new Timeline();
        final KeyValue kv1 = new KeyValue(map_scrollpane.hvalueProperty(), scrollH);
        final KeyValue kv2 = new KeyValue(map_scrollpane.vvalueProperty(), scrollV);
        final KeyFrame kf  = new KeyFrame(Duration.millis(500), kv1, kv2);
        timeline.getKeyFrames().add(kf);
        timeline.play(); // Inicia la animación (no bloquea el hilo de la UI)

    }

    private void buildMap(File imgFile) {
        if (!imgFile.exists()) {
            if (mapPane == null) {
                mapPane = new Pane();
                if (imagenMapa != null) mapPane.getChildren().add(imagenMapa);
                zoomGroup = new Group();
                zoomGroup.getChildren().add(mapPane);
                Group contentGroup = new Group();
                contentGroup.getChildren().add(zoomGroup);
                map_scrollpane.setContent(contentGroup);
            }
            return;
            }

        Image img = new Image(imgFile.toURI().toString());
        double W = img.getWidth();
        double H = img.getHeight();

        mapPane = new Pane();
        mapPane.setPrefSize(W, H); 
        mapPane.setMinSize(W, H);  
        mapPane.setMaxSize(W, H);  

        ImageView iv = new ImageView(img);
        iv.setFitWidth(W);
        iv.setFitHeight(H);
        mapPane.getChildren().add(iv);

        mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                onMapRightClick(e.getX(), e.getY());
            } else if (e.getButton() == MouseButton.PRIMARY && insertionMode) {
                insertionMode = false;
                mapPane.setStyle(""); 
                addPoi(e.getX(), e.getY());
            }
        });

        zoomGroup = new Group();
        Group contentGroup = new Group();
        zoomGroup.getChildren().add(mapPane);
        contentGroup.getChildren().add(zoomGroup);

        double zoom = zoom_slider.getValue();
        zoomGroup.setScaleX(zoom);
        zoomGroup.setScaleY(zoom);

        map_scrollpane.setContent(contentGroup);
    }
    private void onMapRightClick(double x, double y) {
        mapContextMenu.hide();
        final double clickX = x;
        final double clickY = y;
        mapContextMenu.getItems().get(0).setOnAction(e -> addPoi(clickX, clickY));
        mapContextMenu.getItems().get(1).setOnAction(e -> addCircle(clickX, clickY));

        mapContextMenu.show(
            mapPane.getScene().getWindow(),
            mapPane.localToScreen(x, y).getX(),
            mapPane.localToScreen(x, y).getY()
        );
    }
    @FXML
    private void about(ActionEvent event) {
        Alert mensaje = new Alert(Alert.AlertType.INFORMATION);
        mensaje.setTitle("Acerca de");
        mensaje.setHeaderText("IPC - 2026");
        mensaje.showAndWait(); 
    }
    private void addPoi(double x, double y) {
        Dialog<Poi> poiDialog = new Dialog<>();
        poiDialog.setTitle("Nuevo POI");
        poiDialog.setHeaderText("Introduce un nuevo POI");

        ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        poiDialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Nombre del POI");

        VBox vbox = new VBox(10, new Label("Nombre:"), nameField);
        poiDialog.getDialogPane().setContent(vbox);

        poiDialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return new Poi(nameField.getText().trim(), x, y);
            }
            return null;
        });
    
    Optional<Poi> result = poiDialog.showAndWait();
    if (result.isPresent()) {
            Poi poi = result.get();
            poi.setPosition(new Point2D(x, y));
            if (map_listview != null) map_listview.getItems().add(poi);

            Text text = new Text(poi.getCode());
            text.setX(x);
            text.setY(y);
            mapPane.getChildren().add(text);
        }
    }
    @FXML
    private void cambiarMapa(ActionEvent event) throws IOException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(".")); 

        File imgFile = fc.showOpenDialog(zoom_slider.getScene().getWindow());

        if (imgFile != null) {
            System.out.println("Mapa seleccionado: " + imgFile.getCanonicalPath());
            buildMap(imgFile); 
            if (map_listview != null) map_listview.getItems().clear(); 
        }
    }

    private void addCircle(double x, double y) {
        Circle circle = new Circle(10, Color.RED); 
        circle.setCenterX(x);
        circle.setCenterY(y);
        mapPane.getChildren().add(circle); 
    }
}

    


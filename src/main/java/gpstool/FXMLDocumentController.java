/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gpstool;

import com.gluonhq.impl.maps.ImageRetriever;
import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPolygon;
import com.gluonhq.maps.MapView;
import com.gluonhq.maps.demo.PoiLayer;
import com.gluonhq.maps.demo.PolygonLayer;
import gpstool.maps.MapboxMapStyle;
import gpstool.maps.OSMMapStyle;
import gpstool.model.Coordinate;
import gpstool.model.GPSDataSet;
import gpstool.model.GPSDataStorage;
import gpstool.model.GPSEntry;
import gpstool.model.GPSPositionEntry;
import gpstool.parser.NMEADataParser;
import gpstool.ui.DataSetListViewCell;
import gpstool.util.PrimaryStageAware;
import gpstool.util.Project;
import gpstool.util.ProjectSerializer;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.dialog.ProgressDialog;

/**
 *
 * @author Thomas
 */
public class FXMLDocumentController implements Initializable, PrimaryStageAware {

    private static final Logger LOGGER = Logger.getLogger(FXMLDocumentController.class.getName());

    @FXML
    public Parent root;
    @FXML
    public Stage parentStage;

    @FXML
    private ListView fileList;
    @FXML
    private BorderPane mapBp;
    @FXML
    private MenuItem menuImport;
    @FXML
    private MenuItem menuClose;
    @FXML
    private CheckMenuItem menuMap;
    @FXML
    private CheckMenuItem menuSat;
    @FXML
    private MenuItem menuSave;
    @FXML
    private MenuItem menuLoad;
    @FXML
    private Label laPropDesc;
    @FXML
    private Slider minSetSlider;
    @FXML
    private Slider maxSetSlider;
    @FXML
    private Label laStartSlider;
    @FXML
    private Label laEndSlider;

    private GPSDataStorage store = new GPSDataStorage();
    private final Map<String, PolygonLayer> layerSet = new HashMap<>();
    private Coordinate[] initBounds;
    private MapView view;

    private List<ChangeListener<Number>> sliderListeners = new LinkedList<>();

    public void setupMap() {
        view = new MapView();
        view.setCenter(51.481846, 7.216236);

        PoiLayer pl = new PoiLayer();
        view.addLayer(pl);
        view.setZoom(3);
        mapBp.setCenter(view);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // init components
        fileList.setItems((ObservableList<GPSDataSet>) store.getSets());
        fileList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileList.setCellFactory(new Callback<ListView<GPSDataSet>, ListCell<GPSDataSet>>() {
            @Override
            public ListCell<GPSDataSet> call(ListView<GPSDataSet> param) {
                return new DataSetListViewCell();
            }
        });

        // support for changing active data set
        fileList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue != null) {
                    GPSDataSet ent = (GPSDataSet) fileList.getSelectionModel().selectedItemProperty().get();
                    laPropDesc.setText(ent.getId());

                    PolygonLayer pl = layerSet.get(ent.getId());

                    /* remove old listeners */
                    for (ChangeListener<Number> li : sliderListeners) {
                        minSetSlider.valueProperty().removeListener(li);
                        maxSetSlider.valueProperty().removeListener(li);
                    }
                    sliderListeners.clear();

                    /* set up sliders */
                    minSetSlider.setMin(1);
                    minSetSlider.setMax(ent.getEntries().size() - 1);
                    minSetSlider.setDisable(false);
                    minSetSlider.setMajorTickUnit(10);
                    minSetSlider.setMinorTickCount(0);
                    if (pl.getBoundaries()[0] > -1) {
                        minSetSlider.setValue(pl.getBoundaries()[0]);
                    } else {
                        minSetSlider.setValue(1);
                    }
                    ChangeListener<Number> minCl = new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                            if (newValue.intValue() >= maxSetSlider.getValue() && maxSetSlider.getValue() < maxSetSlider.getMax()) {
                                maxSetSlider.setValue(newValue.intValue() + 1);
                            }
                            DateFormat f = new SimpleDateFormat("dd.MM.YYYY - hh:mm:ss.s");
                            laStartSlider.setText("Start: " + f.format(ent.getEntries().get(newValue.intValue()).getDate()));
                            pl.setBoundaries(newValue.intValue(), ((Double) maxSetSlider.getValue()).intValue());
                        }
                    };
                    sliderListeners.add(minCl);
                    minSetSlider.valueProperty().addListener(minCl);

                    maxSetSlider.setMin(1);
                    maxSetSlider.setMax(ent.getEntries().size() - 1);
                    maxSetSlider.setDisable(false);
                    maxSetSlider.setValue(ent.getEntries().size());
                    maxSetSlider.setMajorTickUnit(10);
                    maxSetSlider.setMinorTickCount(0);
                    if (pl.getBoundaries()[1] > -1) {
                        maxSetSlider.setValue(pl.getBoundaries()[1]);
                    } else {
                        maxSetSlider.setValue(((Double) maxSetSlider.getMax()).intValue());
                    }

                    ChangeListener<Number> maxCl = new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                            if (newValue.intValue() <= minSetSlider.getValue() && minSetSlider.getValue() > 0) {
                                minSetSlider.setValue(newValue.intValue() - 1);
                            }
                            PolygonLayer pl = layerSet.get(ent.getId());
                            DateFormat f = new SimpleDateFormat("dd.MM.YYYY - hh:mm:ss.s");
                            laEndSlider.setText("End: " + f.format(ent.getEntries().get(newValue.intValue()).getDate()));
                            pl.setBoundaries(((Double) minSetSlider.getValue()).intValue(), newValue.intValue());
                        }
                    };
                    sliderListeners.add(maxCl);
                    maxSetSlider.valueProperty().addListener(maxCl);
                }
            }
        });

        // setup the rest
        menuImport.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                FileChooser fc = new FileChooser();
                fc.setInitialDirectory(
                        new File(System.getProperty("user.home"))
                );
                fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("NMEA Files (*.nmea)", "*.nmea"), new FileChooser.ExtensionFilter("All Files", "*.*"));
                List<File> ofiles = fc.showOpenMultipleDialog(root.getScene().getWindow());

                if (!ofiles.isEmpty()) {
                    // Import Task
                    Task<Void> importTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            NMEADataParser parser = new NMEADataParser();
                            ofiles.stream().forEach((f) -> {
                                updateMessage("Parsing <" + f.getName() + ">");
                                store.addSet(parser.parseGPSFile(f));
                            });

                            // process import
                            Random r = new Random();
                            store.getSets().stream().forEach((GPSDataSet s) -> {
                                if (!layerSet.containsKey(s.getId())) {
                                    updateMessage("Processing <" + s.getId() + "> data set");
                                    PolygonLayer pl = new PolygonLayer();
                                    pl.setUid(s.getId());
                                    initBounds = s.getBounds();

                                    List<Coordinate> cords = new ArrayList<>();
                                    for (GPSEntry e : s.getEntries()) {
                                        if (e instanceof GPSPositionEntry) {
                                            cords.add(new Coordinate(((GPSPositionEntry) e).getLat(), ((GPSPositionEntry) e).getLon()));
                                        }
                                    }

                                    Random random = new Random(System.currentTimeMillis());
                                    final float hue = random.nextFloat() * 360;
                                    final float saturation = 0.9f;//1.0 for brilliant, 0.0 for dull
                                    final float luminance = 1.0f; //1.0 for brighter, 0.0 for black
                                    Color color = Color.hsb(hue, saturation, luminance);

                                    MapPolygon mp = new MapPolygon(cords.toArray(new Coordinate[cords.size()]), color);
                                    pl.addPoly(mp);
                                    layerSet.put(s.getId(), pl);

                                    if (cords.size() > 0) {
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                System.out.println("Added view");
                                                view.addLayer(pl);
                                            }
                                        });
                                    }
                                }
                            });
                            return null;
                        }
                    };

                    ProgressDialog pd = new ProgressDialog(importTask);
                    pd.setContentText("Importing file....");
                    pd.setHeaderText("Please Wait...");
                    pd.initModality(Modality.WINDOW_MODAL);
                    pd.initOwner(parentStage);
                    pd.show();
                    importTask.setOnSucceeded((sevent) -> {
                        //zoom to mid of bounds
                        double midLat = initBounds[1].latitude - (initBounds[1].latitude - initBounds[0].latitude) / 2.0;
                        double midLon = initBounds[1].longtitude - (initBounds[1].longtitude - initBounds[0].longtitude) / 2.0;

                        view.setCenter(midLat, midLon);
                        view.setZoom(10);
                    });
                    Thread thr = new Thread(importTask);
                    thr.start();
                }
            }
        }
        );

        menuSat.setOnAction(
                (event) -> {
                    ImageRetriever.setMapStyle(new MapboxMapStyle());
                    menuMap.selectedProperty().setValue(false);
                    menuSat.selectedProperty().setValue(true);
                    view.clearTiles();
                }
        );
        menuMap.setOnAction(
                (event) -> {
                    ImageRetriever.setMapStyle(new OSMMapStyle());
                    menuMap.selectedProperty().setValue(true);
                    menuSat.selectedProperty().setValue(false);
                    view.clearTiles();
                }
        );
        menuSave.setOnAction(
                (event) -> {
                    FileChooser fc = new FileChooser();
                    fc.setInitialDirectory(
                            new File(System.getProperty("user.home"))
                    );
                    fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Project Files (*.gppr)", "*.gppr"), new FileChooser.ExtensionFilter("All Files", "*.*"));
                    File ofile = fc.showSaveDialog(root.getScene().getWindow());

                    if (ofile != null) {
                        Map<String, String> settings = new HashMap<>();
                        ArrayList<MapLayer> layers = new ArrayList<>(view.getLayers());
                        Object proj = ProjectSerializer.packProject(store, layers, settings);
                        ProjectSerializer.saveGZIPObject(proj, ofile.getAbsolutePath());
                        
                        parentStage.setTitle("GPSSuite - "+ofile.getName());
                    }
                }
        );
        menuLoad.setOnAction(
                (event) -> {

                    FileChooser fc = new FileChooser();
                    fc.setInitialDirectory(
                            new File(System.getProperty("user.home"))
                    );
                    fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Project Files (*.gppr)", "*.gppr"), new FileChooser.ExtensionFilter("All Files", "*.*"));
                    File ofile = fc.showOpenDialog(root.getScene().getWindow());

                    if (ofile != null) {
                        // clear previous state
                        view.getLayers().clear();
                        layerSet.clear();

                        Project proj = (Project) ProjectSerializer.loadGZipObject(ofile.getAbsolutePath());
                        for (MapLayer lay : proj.layers) {
                            if (lay instanceof PolygonLayer) {
                                layerSet.put(((PolygonLayer) lay).getUid(), (PolygonLayer) lay);
                                view.addLayer(lay);
                                ((PolygonLayer) lay).refresh();
                            }
                        }
                        store = proj.storage;
                        fileList.setItems((ObservableList<GPSDataSet>) store.getSets());

                        parentStage.setTitle("GPSSuite - " + ofile.getName());
                    }
                }
        );
        menuClose.setOnAction(
                (event) -> {
                    parentStage.close();
                }
        );
    }

    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.parentStage = primaryStage;
    }

}

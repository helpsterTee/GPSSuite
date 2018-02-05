/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gpstool;

import gpstool.util.PrimaryStageAware;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Thomas
 */
public class GPSTool extends Application {

    public static Properties prop = new Properties();
    private static final Logger logger = Logger.getLogger( GPSTool.class.getName() );

    @Override
    public void start(Stage stage) throws Exception {
        //settings
        File settings = new File(System.getProperty("user.dir")+"/build/resources/main/credentials.properties");
        InputStream input = null;
        if (settings.exists()) {
            input = new FileInputStream(settings);
            GPSTool.prop.load(input);
            logger.log(Level.INFO, "API Key retrieved: "+prop.getProperty("MapBoxApiKey"));
        } else {
            logger.log(Level.INFO, "No settings file found.");
        }

        //fxml
        URL url = getClass().getResource("FXMLDocument.fxml");
        final FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/gpstool/FXMLDocument.fxml"
                )
        );

        final Parent root = (Parent) loader.load();
        final FXMLDocumentController controller = loader.getController();
        controller.root = root;

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
        
        ((PrimaryStageAware)controller).setPrimaryStage(stage);

        // late load the map view
        controller.setupMap();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

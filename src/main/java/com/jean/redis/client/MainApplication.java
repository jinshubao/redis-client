package com.jean.redis.client;

import com.jean.redis.client.util.ResourceLoader;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ComponentScan
public class MainApplication extends Application {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    private ConfigurableApplicationContext applicationContext;


    @Override
    public void init() throws Exception {
        List<String> params = getParameters().getRaw();
        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_INIT, this));
        applicationContext = SpringApplication.run(MainApplication.class, params.toArray(new String[0]));
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START, this));
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(param -> applicationContext.getBean(param));
        Parent root = loader.load(ResourceLoader.loadAsStream("/fxml/Scene.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setTitle(applicationName + " - " + applicationVersion);
        stage.getIcons().add(new Image(ResourceLoader.loadAsStream("/image/dbs_redis_32px.png")));
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, "确认退出？", ButtonType.CANCEL, ButtonType.OK);
            dialog.setHeaderText(null);
            dialog.setTitle("退出提示");
            Stage sta = (Stage) dialog.getDialogPane().getScene().getWindow();
            sta.getIcons().add(new Image(ResourceLoader.loadAsStream("/image/dbs_redis_24px.png")));
            dialog.showAndWait().ifPresent(buttonType -> {
                if (buttonType != ButtonType.OK) {
                    event.consume();
                }
            });
        });
    }

    @Override
    public void stop() throws Exception {
        applicationContext.close();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

package com.jean.redis.client;

import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class MainApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() throws Exception {
        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_INIT, this));
        applicationContext = new AnnotationConfigApplicationContext(MainApplication.class);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START, this));
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(param -> applicationContext.getBean(param));
        Parent root = loader.load(getClass().getResourceAsStream("/fxml/Scene.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        String name = applicationContext.getEnvironment().getProperty("spring.application.name", "redis client");
        String version = applicationContext.getEnvironment().getProperty("spring.application.version", "1.0");
        stage.setTitle(name + " - " + version);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/image/dbs_redis_32px.png")));
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, "确认退出？", ButtonType.CANCEL, ButtonType.OK);
            dialog.setHeaderText(null);
            dialog.setTitle("退出提示");
            Stage sta = (Stage) dialog.getDialogPane().getScene().getWindow();
            sta.getIcons().add(new Image(this.getClass().getResourceAsStream("/image/dbs_redis_24px.png")));
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

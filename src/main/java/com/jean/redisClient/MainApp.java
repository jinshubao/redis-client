package com.jean.redisClient;

import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MainApp extends Application {

    private static String[] args;
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() throws Exception {
        applicationContext = SpringApplication.run(getClass(), args);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void start(Stage stage) throws Exception {

        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START));

        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(param -> applicationContext.getBean(param));
        Parent root = loader.load(getClass().getResourceAsStream("/fxml/Scene.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setTitle("redis client - v1.0");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/image/dbs_redis_48px_.png")));
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("退出提示");
            dialog.setContentText("\r\n\t确定退出？\r\n");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
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
        MainApp.args = args;
        launch(args);
    }

}

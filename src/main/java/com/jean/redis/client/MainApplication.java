package com.jean.redis.client;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.factory.RedisThreadFactory;
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
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    private Map<Class<?>, Object> controllers = new HashMap<>();

    private ExecutorService executorService;

    private ResourceBundle bundle;

    private Callback<Class<?>, Object> controllerFactory;

    private URL mainUI;

    @Override
    public void init() throws Exception {
        //启动参数
        List<String> params = getParameters().getRaw();
        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_INIT, this));
        executorService = Executors.newFixedThreadPool(CommonConstant.THREAD_POOL_SIZE, new RedisThreadFactory());
        bundle = ResourceBundle.getBundle("local", Locale.SIMPLIFIED_CHINESE, new EncodingResourceBundleControl("UTF-8"));
        mainUI = ResourceLoader.load("/fxml/Scene.fxml");
        controllerFactory = param -> {
            try {
                //控制器注入线程池
                Constructor<?> constructor = param.getDeclaredConstructor(ExecutorService.class);
                Object instance = constructor.newInstance(executorService);
                controllers.put(param, instance);
                return instance;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public void start(Stage stage) throws Exception {
        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START, this));
        FXMLLoader loader = new FXMLLoader(mainUI, bundle, null, controllerFactory);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setTitle("redis client" + " - " + "1.0.0");
        stage.getIcons().add(new Image(ResourceLoader.Image.redis_logo_32));
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, bundle.getString("application.dialog.exit.confirm"), ButtonType.CANCEL, ButtonType.OK);
            dialog.setHeaderText(null);
            dialog.setTitle(bundle.getString("application.dialog.exit.title"));
            Stage window = (Stage) dialog.getDialogPane().getScene().getWindow();
            window.getIcons().add(new Image(ResourceLoader.Image.redis_logo_24));
            dialog.showAndWait().ifPresent(buttonType -> {
                if (buttonType != ButtonType.OK) {
                    event.consume();
                }
            });
        });
    }

    @Override
    public void stop() throws Exception {
        controllers.values().forEach(value -> {
            if (value instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) value).close();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
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

    /**
     * ResourceBundle 编码
     */
    private static final class EncodingResourceBundleControl extends ResourceBundle.Control {

        private final String encoding;

        private EncodingResourceBundleControl(String encoding) {
            this.encoding = encoding;
        }

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            URL resourceURL = loader.getResource(resourceName);
            if (resourceURL != null) {
                return new PropertyResourceBundle(new InputStreamReader(resourceURL.openStream(), encoding));
            }
            return super.newBundle(baseName, locale, format, loader, reload);
        }
    }
}

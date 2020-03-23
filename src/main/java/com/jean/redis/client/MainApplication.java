package com.jean.redis.client;

import com.jean.redis.client.constant.CommonConstant;
import com.jean.redis.client.factory.ControllerFactory;
import com.jean.redis.client.mange.TaskManger;
import com.jean.redis.client.util.ResourceLoader;
import com.jean.redis.client.util.StringUtils;
import io.lettuce.core.AbstractRedisClient;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.pool2.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author jinshubao
 */
public class MainApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    private List<String> params;

    private Map<Class<?>, Object> controllers = new HashMap<>();

    private ResourceBundle bundle;

    @Override
    public void init() throws Exception {
        //启动参数
        params = getParameters().getRaw();
        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_INIT, this));
        bundle = ResourceBundle.getBundle("local", Locale.getDefault(), new EncodingResourceBundleControl());
    }

    @Override
    public void start(Stage stage) throws Exception {
        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START, this));
        URL resource = getClass().getResource("/fxml/Scene.fxml");
        FXMLLoader loader = new FXMLLoader(resource, bundle, null, new ControllerFactory());
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        String title = StringUtils.join(params, " ");
        if (StringUtils.isEmpty(title)) {
            title = "redis client";
        }
        stage.setTitle(title);
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
        CommonConstant.GLOBAL_REDIS_CONNECTION_POOL_CACHE.values().forEach(ObjectPool::close);
        CommonConstant.GLOBAL_REDIS_CLIENT_CACHE.values().forEach(AbstractRedisClient::shutdownAsync);
        TaskManger.getInstance().shutdown();
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

        private final Charset encoding;

        EncodingResourceBundleControl() {
            this.encoding = StandardCharsets.UTF_8;
        }

        private EncodingResourceBundleControl(Charset encoding) {
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

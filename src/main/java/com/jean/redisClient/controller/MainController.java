package com.jean.redisClient.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class MainController implements Initializable {

    @FXML
    public TextField keyword;
    @FXML
    public Button search;
    @FXML
    public ListView<String> list;
    @FXML
    private TreeView<Object> dbTreeview;
    @FXML
    private VBox dataList;
    @FXML
    private SplitPane splitPane;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        search.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String key = keyword.getText();
                if (key != null && !key.trim().isEmpty()) {
                    String value = redisTemplate.boundValueOps(key).get();
                    list.getItems().add(value);
                }
                Long size = redisTemplate.opsForList().size(key);
                System.out.println(size);
            }
        });
    }
}

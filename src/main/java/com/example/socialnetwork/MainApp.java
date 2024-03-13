package com.example.socialnetwork;

import com.example.socialnetwork.controller.LogInController;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.validators.FriendshipValidator;
import com.example.socialnetwork.domain.validators.UserValidator;
import com.example.socialnetwork.repository.*;
import com.example.socialnetwork.service.Service;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    private Service service;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        UserRepository userRepo = new UserDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres", new UserValidator());
        FriendshipRepository friendshipRepo = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres", new FriendshipValidator());
        Repository<Long, Message> messageRepo = new MessageDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres", userRepo);

        service = new Service(userRepo, friendshipRepo, messageRepo);

        initView(primaryStage);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Socialnetwork");
        primaryStage.setScene(scene);

        LogInController logController = fxmlLoader.getController();
        logController.setService(this.service);
    }
}
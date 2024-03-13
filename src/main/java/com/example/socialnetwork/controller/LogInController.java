package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.service.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LogInController {
    private Service service;

    @FXML
    private TextField email;
    @FXML
    private PasswordField password;

    public void setService(Service service) {
        this.service = service;
    }

    @FXML
    public void onLogInButtonClick(ActionEvent actionEvent) {
        User user = service.getUserByEmail(email.getText());

        try{
            if(user == null) {
                MessageAlert.showErrorMessage(null, "User not found");
            }
            else if(password.getText().isEmpty()) {
                MessageAlert.showErrorMessage(null, "Password must not be empty");
            }
            else if(!password.getText().equals(user.getPassword())) {
                MessageAlert.showErrorMessage(null, "Incorrect password");
            }
            else {
                FXMLLoader stageLoader = new FXMLLoader();
                stageLoader.setLocation(getClass().getResource("/com/example/socialnetwork/views/users-view.fxml"));
                TabPane appLayout = stageLoader.load();

                Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(appLayout);
                stage.setScene(scene);

                AppController appController = stageLoader.getController();
                appController.setService(this.service, String.valueOf(email.getText()));
                appController.initApp();
            }
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    @FXML
    public void onSingUpClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader stageLoader = new FXMLLoader();
        stageLoader.setLocation(getClass().getResource("/com/example/socialnetwork/views/signup-view.fxml"));
        AnchorPane signUpLayout = stageLoader.load();

        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(signUpLayout);
        stage.setScene(scene);

        SignUpController signUpController = stageLoader.getController();
        signUpController.setService(this.service);

        stage.show();
    }
}

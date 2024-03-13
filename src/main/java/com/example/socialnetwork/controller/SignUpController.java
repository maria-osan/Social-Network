package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.domain.validators.UserValidator;
import com.example.socialnetwork.service.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {
    private Service service;
    public UserValidator validator = new UserValidator();

    @FXML
    private TextField first_name;
    @FXML
    private TextField last_name;
    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private TextField password_confirm;

    public void setService(Service service) {
        this.service = service;
    }

    @FXML
    public void onCreateAccountButtonClick(ActionEvent actionEvent) {
        try {
            if(password.getText().isEmpty()) {
                MessageAlert.showErrorMessage(null, "Password must not be empty");
            }
            else if(!password.getText().equals(password_confirm.getText())) {
                MessageAlert.showErrorMessage(null, "Incorrect password");
            }
            else if(service.getUserByEmail(email.getText()) != null && password.getText().equals(service.getUserByEmail(email.getText()).getPassword())) {
                MessageAlert.showErrorMessage(null, "The account already exist");
            } else if (service.getUserByEmail(email.getText()) != null && !password.getText().equals(service.getUserByEmail(email.getText()).getPassword())) {
                MessageAlert.showErrorMessage(null, "The account already exist with another password");
            } else {
                User newUser = new User(first_name.getText(), last_name.getText(), email.getText(), password.getText());

                try{
                    validator.validate(newUser);
                } catch (Exception e) {
                    MessageAlert.showErrorMessage(null, e.getMessage());
                }

                service.addUser(newUser);

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
    public void onLogInClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader stageLoader = new FXMLLoader();
        stageLoader.setLocation(getClass().getResource("/com/example/socialnetwork/views/login-view.fxml"));
        AnchorPane logInLayout = stageLoader.load();

        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(logInLayout);
        stage.setScene(scene);

        LogInController logInController = stageLoader.getController();
        logInController.setService(this.service);

        stage.show();
    }
}

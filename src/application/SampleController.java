package application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SampleController {
	@FXML 
	private TextField EmailInput;
	@FXML
	private PasswordField PasswordInput;
	@FXML
	private Button SigninButton;
	@FXML
	private Label RegisterButton;
	@FXML
	private Label ErrorLabel;


	Connection conn = null;
	
	
	@FXML
	public void SignInAction(ActionEvent event) {
	    if (EmailInput.getText().isBlank() || PasswordInput.getText().isBlank()) {
	        ErrorLabel.setText("*You must fill all fields!");
	        ErrorLabel.setStyle("-fx-background-color: #f86464;");
	    } else {
	        try (Connection conn = DatabaseConnection.ConnectDB();
	            PreparedStatement preparedStatement = conn.prepareStatement("SELECT COUNT(1) FROM user_account WHERE email=? AND password=?")) {
	            String email = EmailInput.getText();
	            String password = PasswordInput.getText();
	            String hashedPassword = hashPassword(password);

	            preparedStatement.setString(1, email);
	            preparedStatement.setString(2, hashedPassword);

	            ResultSet queryResult = preparedStatement.executeQuery();

	            if (queryResult.next()) {
	                int count = queryResult.getInt(1);
	                if (count == 1) {
	                    // Successful login
	                    ErrorLabel.setText("");
	                    ErrorLabel.setStyle("-fx-background-color: #fff;");
	            		Stage stage = (Stage) RegisterButton.getScene().getWindow();
	            		stage.close();
	            		Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
	            		Stage sentScene = new Stage();
	            		sentScene.setScene(new Scene(root, 400, 400));
	            		Image icon = new Image("C:/Users/Admin/eclipse-workspace/GmailClone/Images/gmail.png");
	            		sentScene.getIcons().add(icon);
	            		sentScene.setTitle("Inbox");
	            		sentScene.setMaximized(true);
	            	
	            		SessionHandler.getInstance().setCurrentUser(email);

	            		
	                    
	                    try (Connection connection = DatabaseConnection.ConnectDB();
	                           PreparedStatement prepStmt = conn.prepareStatement("UPDATE user_account SET active = true WHERE email = ?")) {
	                    	prepStmt.setString(1, email);
	                    	prepStmt.executeUpdate();
	                       } catch (Exception e) {
	                           e.printStackTrace();
	                       }
	                	sentScene.show();
	                    
	                    
	                } else {
	                    ErrorLabel.setText("*Incorrect email or password");
	                    ErrorLabel.setStyle("-fx-background-color: #f86464;");
	                }
	            }
	        } catch (Exception e) {
	           e.printStackTrace();
	        }
	    }
	}

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
            byte[] hashBytes = digest.digest(passwordBytes);

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = String.format("%02x", b);
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	
	@FXML
	public void OpenRegisterForm(MouseEvent event) {
		try {
			Stage stage = (Stage) RegisterButton.getScene().getWindow();
			stage.close();
			Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
			Stage registerScene = new Stage();
			registerScene.setScene(new Scene(root, 600, 400));
			Image icon = new Image("C:/Users/Admin/eclipse-workspace/GmailClone/Images/gmail.png");
			registerScene.getIcons().add(icon);
			registerScene.setTitle("Create account");
			registerScene.show();
			registerScene.setResizable(false);
		} catch(Exception e) {
			e.printStackTrace();
			e.getCause();
		}
	}
	
	
	
	
	
}

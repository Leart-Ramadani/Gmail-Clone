package application;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;



public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {

    	try {
                		
    		BorderPane root = (BorderPane) FXMLLoader.load(getClass().getResource("Login.fxml"));
            Scene scene = new Scene(root, 400, 400);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            primaryStage.setScene(scene);

            Image icon = new Image("C:/Users/Admin/eclipse-workspace/GmailClone/Images/gmail.png");
            primaryStage.getIcons().add(icon);
            primaryStage.setTitle("Login");
            primaryStage.setResizable(false);
            primaryStage.show();
            } catch(Exception ex) {
            	ex.printStackTrace();
            }
    	
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
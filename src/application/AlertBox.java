package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class AlertBox {
	
	public static void display(String title, String content) {
		Stage alertWindow = new Stage();
		alertWindow.initModality(Modality.APPLICATION_MODAL);
		alertWindow.setTitle(title);
		alertWindow.setMinWidth(250);
		
		Label label = new Label();
		label.setText(content);
		
		Button closeButton = new Button("Close");
		closeButton.setOnAction(e->alertWindow.close());
		
		VBox layout = new VBox(20);
		layout.setPadding(new Insets(30));
		layout.getChildren().addAll(label, closeButton);
		layout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(layout);
		alertWindow.setScene(scene);
		alertWindow.setResizable(false);
		alertWindow.showAndWait();
	}
	
}

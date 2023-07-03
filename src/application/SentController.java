package application;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;


public class SentController implements Initializable{


	@FXML
	private AnchorPane popProfile;
	@FXML
	private Button profileIcon;
	@FXML
	private AnchorPane Compose;
	@FXML
	private AnchorPane SignOutButton;
	@FXML
	private AnchorPane InboxPage;
	@FXML
	private TextField SendTo;
	@FXML
	private TextField Subject;
	@FXML
	private TextArea Mail;
	@FXML
	private TableView<Emails> emailTable;
	@FXML
	private TableColumn<Emails, String> SendToColumn;
	@FXML
	private TableColumn<Emails, String> SubjectColumn;
	@FXML 
	private TableColumn<Emails, String> MailColumn;
	@FXML
	private TableColumn<Emails, Timestamp> DateColumn;
	@FXML
	private Button ManageButton;
	@FXML
	private Label usernameLabel;
	@FXML
	private Label emailLabel;
	@FXML
	private Button popIcon;
	
	@FXML
	private AnchorPane EmailsTop;
	
	@FXML
	private AnchorPane SpecificMail;
	@FXML
	private Label subjectArea;
	@FXML
	private Label nameArea;
	@FXML
	private Label emailArea;
	@FXML
	private Label dataArea;
	@FXML
	private TextArea mailArea;
	@FXML
	private AnchorPane BackMailButton;
	
	@FXML
	private AnchorPane deleteMail;
	
	
	Connection conn = null;
	
	String currentUser = SessionHandler.getCurrentUser();
	
	
	@FXML
	public void ShowProfile(ActionEvent event) {
		if(popProfile.isVisible()) {			
			popProfile.setVisible(false);
		} else {
			popProfile.setVisible(true);		
		    String email = currentUser;
	        String firstLetter = email.substring(0, 1);
	        popIcon.setText(firstLetter.toUpperCase());
	        emailLabel.setText(email);
	        
	        conn = DatabaseConnection.ConnectDB();
	        String query = "SELECT name, lastName FROM user_account WHERE email=?";
	        try {
	        	PreparedStatement prepStmt = conn.prepareStatement(query);
	        	
	        	prepStmt.setString(1, email);
	        	
	        	ResultSet resultSet = prepStmt.executeQuery();
	        	
	        	while(resultSet.next()) {
	        		String name = resultSet.getString("name");
	        		String lastName = resultSet.getString("lastName");
	        		
	        		
	        		usernameLabel.setText(name + " " + lastName);
	        	}
	        	
	        resultSet.close();
	        prepStmt.close();
	        conn.close();
	        	
	        } catch(Exception ex) {
	        	ex.printStackTrace();
	        }
		}
	}
	
	@FXML
	public void CloseCompose(MouseEvent event) {
		SendTo.setText("");
		Subject.setText("");
		Mail.setText("");
		Compose.setVisible(false);
	}
	
	@FXML 
	public void ShowCompose(MouseEvent event) {
		Compose.setVisible(true);
		AnchorPane.setRightAnchor(Compose, 10.0);
		AnchorPane.setBottomAnchor(Compose, 0.0);
	}
	
	@FXML
	public void SignOut(MouseEvent event) {
		try {
			Stage stage = (Stage) SignOutButton.getScene().getWindow();
			stage.close();
			Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
			Stage registerScene = new Stage();
			registerScene.setScene(new Scene(root, 400, 400));
			Image icon = new Image("C:/Users/Admin/eclipse-workspace/GmailClone/Images/gmail.png");
			registerScene.getIcons().add(icon);
			registerScene.setTitle("Login");
			registerScene.show();
			registerScene.setResizable(false);
			SessionHandler.getInstance().clearSession();
		} catch(Exception e) {
			e.printStackTrace();
			e.getCause();
		}
	}
	
	
	@FXML
	public void SendEmail(ActionEvent event) {
		SendEmail sendEmail = new SendEmail(currentUser, SendTo, Subject, Mail);
		sendEmail.send_email(currentUser, sendEmail.getSendTo(), sendEmail.getSubject(), sendEmail.getMail());
		CloseCompose(null);
		showEmails();
	}
	
	public ObservableList<Emails> getEmails(){
		ObservableList<Emails> listOfEmails = FXCollections.observableArrayList();
		conn = DatabaseConnection.ConnectDB();
		String query = "SELECT id, send_by, send_to, subject, mail, send_time FROM mails WHERE send_by='" + currentUser + "'";
		Statement statement;
		ResultSet queryResult;
		
		try {
			statement = conn.createStatement();
			queryResult = statement.executeQuery(query);
			Emails emails;
			while(queryResult.next()) {
				emails = new Emails(queryResult.getInt("id"), queryResult.getString("send_by"), queryResult.getString("send_to"), queryResult.getString("subject"), 
						queryResult.getString("mail"), queryResult.getTimestamp("send_time"));
				listOfEmails.add(emails);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return listOfEmails;
	}
	
	public void showEmails() {
	    ObservableList<Emails> email = getEmails();
	    SendToColumn.setCellValueFactory(cellData -> new SimpleStringProperty("To: "+cellData.getValue().getSendTo()));
	    SubjectColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSubject()));
	    MailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMail()));
	    DateColumn.setCellValueFactory(cellData -> {
	        SimpleObjectProperty<Timestamp> property = new SimpleObjectProperty<>(cellData.getValue().getDate());
	        return property;
	    });
	    
	    emailTable.setItems(email);
	}

	@FXML
	public void InboxPage(MouseEvent event) {
		try {
			Stage stage = (Stage) InboxPage.getScene().getWindow();
			stage.close();
			Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
			Stage sentScene = new Stage();
			sentScene.setScene(new Scene(root, 400, 400));
			Image icon = new Image("C:/Users/Admin/eclipse-workspace/GmailClone/Images/gmail.png");
			sentScene.getIcons().add(icon);
			sentScene.setTitle("Inbox");
			sentScene.setMaximized(true);
			sentScene.show();
		} catch(Exception e) {
			e.printStackTrace();
			e.getCause();
		}
	}
	
	
	@FXML
	public void OpenManageAccount(ActionEvent event) {
		try {
			Stage stage = (Stage) ManageButton.getScene().getWindow();
			stage.close();
			Parent root = FXMLLoader.load(getClass().getResource("ManageAccount.fxml"));
			Stage registerScene = new Stage();
			registerScene.setScene(new Scene(root, 600, 400));
			Image icon = new Image("C:/Users/Admin/eclipse-workspace/GmailClone/Images/gmail.png");
			registerScene.getIcons().add(icon);
			registerScene.setTitle("Manage account");
			registerScene.show();
			registerScene.setResizable(false);
		} catch(Exception e) {
			e.printStackTrace();
			e.getCause();
		}
	}
	
	@FXML
	public void setProfileIcon() {
	    String username = currentUser;
	    if (username != null && !username.isEmpty()) {
	        String firstLetter = username.substring(0, 1);
	        profileIcon.setText(firstLetter.toUpperCase());
	    }
	}


	@FXML
	public void BackMail(MouseEvent event) {
		SpecificMail.setVisible(false);
		emailTable.setVisible(true);
		EmailsTop.setVisible(true);
	}
	

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		showEmails();
		setProfileIcon();
		
		emailTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if(newSelection != null) {
				Emails selectedMail = emailTable.getSelectionModel().getSelectedItem();
				emailTable.setVisible(false);
				EmailsTop.setVisible(false);
				
				
				conn = DatabaseConnection.ConnectDB();
				
				String query = "SELECT name, lastName FROM user_account WHERE email=?";
				
				try {
					PreparedStatement prepStmt = conn.prepareStatement(query);
					
					prepStmt.setString(1, selectedMail.getSendTo());
					
					ResultSet resultSet = prepStmt.executeQuery();
					
					if(resultSet.next()) {
						String name = resultSet.getString("name");
						String lastName = resultSet.getString("lastName");
						
						nameArea.setText(name + " " + lastName);
						subjectArea.setText(selectedMail.getSubject());
						mailArea.setText(selectedMail.getMail());
						emailArea.setText("<"+selectedMail.getSendTo()+">");
			            String formattedDate = formatTimestamp(selectedMail.getDate());
			            dataArea.setText(formattedDate);
						SpecificMail.setVisible(true);
						
			            deleteMail.setOnMouseClicked(event -> {
			                deleteSelectedRow(selectedMail.getId());
			            });
					}
					resultSet.close();
					prepStmt.close();
					conn.close();
				} catch(Exception ex) {
					ex.printStackTrace();
				}

			}
		});
	
	}

	// Method to delete the selected row
	private void deleteSelectedRow(int id) {
	    conn = DatabaseConnection.ConnectDB();
	    String query = "DELETE FROM mails WHERE id = ?";
	    try {
	        PreparedStatement prepStmt = conn.prepareStatement(query);
	        prepStmt.setInt(1, id);
	        prepStmt.executeUpdate();
	        prepStmt.close();
	        conn.close();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    // Refresh the email table after deleting the row
        BackMail(null);
	    showEmails();
	}
	
	
	private String formatTimestamp(Timestamp timestamp) {
	    SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, HH:mm");
	    return formatter.format(timestamp);
	}
}
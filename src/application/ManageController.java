package application;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ManageController extends Application implements Initializable {

	@FXML
	private Label LoginButton;
	@FXML
	private Button updateButton;
	@FXML
	private TextField NameInput;
	@FXML
	private TextField SurnameInput;
	@FXML
	private TextField EmailInput;
	@FXML
	private PasswordField PasswordInput;
	@FXML
	private ComboBox<String> GenderInput;
	@FXML
	private DatePicker BirthdayInput;
	@FXML
	private Label NameError;
	@FXML
	private Label SurnameError;
	@FXML
	private Label BirthdayError;
	@FXML
	private Label GenderError;
	@FXML
	private Label EmailError;
	@FXML
	private Label PasswordError;
	@FXML
	private AnchorPane InboxPage;

	Connection conn = null;
	
	String currentUser = SessionHandler.getCurrentUser();
	
	
	private boolean nameErr;
	private boolean surnameErr;
	private boolean emailErr;
	private boolean passErr;
	private boolean birthdayErr;
	private boolean genderErr;
	
	
	
    private static final String CUSTOM_REGEX = "^[A-Za-z0-9.-]+$";
    private static final Pattern CUSTOM_PATTERN = Pattern.compile(CUSTOM_REGEX);

    public static boolean isValidEmail(String input) {
        return CUSTOM_PATTERN.matcher(input).matches();
    }
	

	
	@FXML
	public void updateUser(ActionEvent event) {
		String regex_text="^[A-Za-z]+";
		
		if(NameInput.getText().isBlank() == true) {
			NameError.setText("*You must fill name input");
			nameErr = false;
		} else if(!NameInput.getText().matches(regex_text)) {
			NameError.setText("*Only letters are available");
			nameErr = false;
		} else {
			NameError.setText("");
			nameErr = true;
		}
		
		if(SurnameInput.getText().isBlank() == true) {
			SurnameError.setText("*You must fill last name input");
			surnameErr = false;
		} else if(!SurnameInput.getText().matches(regex_text)) {
			SurnameError.setText("*Only letters are available");
			surnameErr = false;
		} else {
			SurnameError.setText("");
			surnameErr = true;
		}
		
        LocalDate selectedDate = BirthdayInput.getValue();
        LocalDate today = LocalDate.now();
        LocalDate ageLimit = today.minusYears(16);
        if (selectedDate == null) {
            BirthdayError.setText("*You must select your birthday");
            birthdayErr = false;
        } else if(selectedDate.isAfter(ageLimit)) {
            BirthdayError.setText("*You have to be over 16");
            birthdayErr = false;
        } else {
            BirthdayError.setText("");
            birthdayErr = true;
        }
        
        String gender = GenderInput.getValue();
        if (gender == null) {
        	GenderError.setText("*You must select your gender");
            genderErr = false;
        } else {
        	GenderError.setText("");
            genderErr = true;
        }
        
        
        
        
		if(EmailInput.getText().isBlank() == true) {
			EmailError.setText("*You must fill email input");
			emailErr = false;
		} else if(!isValidEmail(EmailInput.getText())) {
			EmailError.setText("*Invalid email! Only letters, \nnumbers, ' . ', ' - ' are available");
			emailErr = false;
		} else {
			emailErr = true;
		}
		
		if(PasswordInput.getText().isBlank() == true) {
			PasswordError.setText("*You must fill password field");
			passErr = false;
		} else if(PasswordInput.getText().length() < 8) {
			PasswordError.setText("*Password can't be smaller than\n  8 characters");
			passErr = false;
		} else {
			PasswordError.setText("");
			passErr = true;
		}
		
		if(nameErr == true && surnameErr == true && birthdayErr == true && genderErr == true && emailErr == true && passErr == true) {
			conn = DatabaseConnection.ConnectDB();
			String query = "UPDATE user_account SET name=?, lastName=?, birthday=?, gender=?, email=?, password=? WHERE email='" +currentUser +"'";
			
			try {
				PreparedStatement prepStmt = conn.prepareStatement(query);
				
				// Name input
				String name = NameInput.getText();
				// Last name input 
				String lastName = SurnameInput.getText();
				
				// Birthday input
				LocalDate birthday = BirthdayInput.getValue();
				java.sql.Date sqlDate = java.sql.Date.valueOf(birthday);
				
				// Gender input
		        String sqlGender = GenderInput.getValue();
				
		        // Email input
		        String email = EmailInput.getText() + "@gmail.com";
	        
		        // Password input and hashing before storing in database
		        String password = PasswordInput.getText();
	            MessageDigest digest = MessageDigest.getInstance("SHA-256");
	            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
	            byte[] hashBytes = digest.digest(passwordBytes);

	            StringBuilder hexString = new StringBuilder();
	            for (byte b : hashBytes) {
	                String hex = String.format("%02x", b);
	                hexString.append(hex);
	            }
	            String hashedPassword = hexString.toString();

	            
	            prepStmt.setString(1, name);
	            prepStmt.setString(2, lastName);
	            prepStmt.setDate(3, sqlDate);
	            prepStmt.setString(4, sqlGender);
	            prepStmt.setString(5, email);
	            prepStmt.setString(6, hashedPassword);
            
	            prepStmt.execute();
	            
	            AlertBox.display("Alert", "Your account " + email + " has been successfully updated! \nYou need to log in again!");
	            
	            try {
					Stage stage = (Stage) updateButton.getScene().getWindow();
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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}
	
	}
	
	@FXML
	public void setInputs() {
		conn = DatabaseConnection.ConnectDB();
		
		String query = "SELECT * FROM user_account WHERE email=?";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(query);
			
			prepStmt.setString(1, currentUser);
			
        	ResultSet resultSet = prepStmt.executeQuery();
        	
        	if(resultSet.next()) {
        		String name = resultSet.getString("name");
        		String lastName = resultSet.getString("lastName");
        		Date birhday = resultSet.getDate("birthday");
        		String gender = resultSet.getString("gender");
        		String email = resultSet.getString("email");
        		
        		String shortenedEmail = shortenString(email);
        		
        		NameInput.setText(name);
        		SurnameInput.setText(lastName);
        		BirthdayInput.setValue(birhday.toLocalDate());
        		GenderInput.setValue(gender);
        		EmailInput.setText(shortenedEmail);
        		
        	}
			
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
    public static String shortenString(String email) {
        int atIndex = email.indexOf("@"); // Find the index of the '@' symbol
        if (atIndex != -1) { // If the '@' symbol is found
            return email.substring(0, atIndex); // Return the substring before the '@' symbol
        } else { 
            return email; // Return the original string if no '@' symbol is found
        }
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ObservableList<String> list = FXCollections.observableArrayList("Male", "Female");
		GenderInput.setItems(list);
		setInputs();
	}

	@Override
	public void start(Stage arg0) throws Exception {
		setInputs();
		
		
	}


}

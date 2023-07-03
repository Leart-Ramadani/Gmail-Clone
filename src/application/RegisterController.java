package application;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class RegisterController extends Application implements Initializable {

	@FXML
	private Label LoginButton;
	@FXML
	private Button SignUpButton;
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
	

	Connection conn = null;
	
	
	
	@FXML
	public void OpenLoginForm(MouseEvent event) {
		try {
			Stage stage = (Stage) LoginButton.getScene().getWindow();
			stage.close();
			Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
			Stage registerScene = new Stage();
			registerScene.setScene(new Scene(root, 400, 400));
			Image icon = new Image("C:/Users/Admin/eclipse-workspace/GmailClone/Images/gmail.png");
			registerScene.getIcons().add(icon);
			registerScene.setTitle("Login");
			registerScene.show();
			registerScene.setResizable(false);
		} catch(Exception e) {
			e.printStackTrace();
			e.getCause();
		}
	}
	
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
	public void RegisterUser(ActionEvent event) {
		
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
			conn = DatabaseConnection.ConnectDB();
			String emailCheck = "SELECT count(1) FROM user_account WHERE email='"+EmailInput.getText()+"@gmail.com'";
			
			try {
				Statement prepCheck = conn.createStatement();
				ResultSet queryResult= prepCheck.executeQuery(emailCheck);
				
				while(queryResult.next()) {
					if(queryResult.getInt(1)==1) {
						EmailError.setText("*This email is taken");
						emailErr = false;
					}else {
						EmailError.setText("");
						emailErr = true;
					}
				}
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
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
			String query = "INSERT INTO user_account(name, lastName, birthday, gender, email, password, active) VALUES(?,?,?,?,?,?,?)";
			
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
	            prepStmt.setBoolean(7, false);
	            
	            prepStmt.execute();
	            
	            AlertBox.display("Alert", "Your account " + email + " has been successfully \nregisterd! Have fun :)");
	            OpenLoginForm(null);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			





		}
		
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ObservableList<String> list = FXCollections.observableArrayList("Male", "Female");
		GenderInput.setItems(list);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}


}

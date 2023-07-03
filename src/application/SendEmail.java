package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SendEmail {

	private String sendBy;
	private TextField sendTo;
	private TextField subject;
	private TextArea mail;

	public SendEmail(String sendBy, TextField sendTo, TextField subject, TextArea mail) {
		this.sendBy = sendBy;
		this.sendTo = sendTo;
		this.subject = subject;
		this.mail = mail;
	}

	public String getSendBy() {
		return sendBy;
	}

	public void setSendBy(String sendBy) {
		this.sendBy = sendBy;
	}

	public TextField getSendTo() {
		return sendTo;
	}

	public void setSendTo(TextField sendTo) {
		this.sendTo = sendTo;
	}

	public TextField getSubject() {
		return subject;
	}

	public void setSubject(TextField subject) {
		this.subject = subject;
	}

	public TextArea getMail() {
		return mail;
	}

	public void setMail(TextArea mail) {
		this.mail = mail;
	}

	Connection conn = null;
	
	public void send_email(String send_by, TextField send_to, TextField subject, TextArea mail) {
		conn = DatabaseConnection.ConnectDB();
		if(send_to.getText().isBlank() || mail.getText().isBlank()) {
			AlertBox.display("Notification", "'Send to' input and 'mail' input must be filled");
		} else {
			String checkEmail = "SELECT count(1) FROM user_account WHERE email='" + send_to.getText() + "'";
			try{
	            Statement statement = conn.createStatement();
	            ResultSet queryResult = statement.executeQuery(checkEmail);
	            
	            while(queryResult.next()) {
	            	if(queryResult.getInt(1) == 1) {
	            		String query = "INSERT INTO mails(send_by, send_to, subject, mail) VALUES(?, ?, ?, ?)";
	            		try {
	            			PreparedStatement prepStmt = conn.prepareStatement(query);
	            			
	            			prepStmt.setString(1, send_by);
	            			prepStmt.setString(2, send_to.getText());
	            			prepStmt.setString(3, subject.getText());
	            			prepStmt.setString(4, mail.getText());
	            			
	            			prepStmt.execute();
	            			AlertBox.display("Notification", "Email sent successfully to " + send_to.getText());
	            		}catch(Exception ex) {
	            			ex.printStackTrace();
	            		}
	            	} else {
	        			AlertBox.display("Notification", "This email: " + send_to.getText() + " doesn't exists. \nMake sure you write it right!");
	            	}
	            }
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
}

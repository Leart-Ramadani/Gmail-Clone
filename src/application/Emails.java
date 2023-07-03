package application;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Emails {
	private int id;
	private String send_by;
	private String send_to;
	private String subject;
	private String mail;
	private Timestamp date;
	
	public Emails(int id, String send_by, String send_to, String subject, String mail, Timestamp send_time) {
		super();
		this.id = id;
		this.send_by = send_by;
		this.send_to = send_to;
		this.subject = subject;
		this.mail = mail;
		this.date = send_time;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		
		this.id= id;
	}

	public String getSendBy() {
		return send_by;
	}

	public void setSendBy(String send_by) {
		this.send_by = send_by;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getSendTo() {
		return send_to;
	}

	public void setSendTo(String send_to) {
		this.send_to = send_to;
	}
}
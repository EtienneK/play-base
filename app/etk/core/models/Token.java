package etk.core.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Token {

	@Id
	public String uuid;

	@Column(nullable = false)
	public String email;

	@Column(nullable = false)
	public Date creationTime;

	@Column(nullable = false)
	public Date expirationTime;

	@Column(nullable = false)
	public boolean isSignUp;

	public Token(String uuid, String email, Date creationTime, Date expirationTime, boolean isSignUp) {
		this.uuid = uuid;
		this.email = email;
		this.creationTime = creationTime;
		this.expirationTime = expirationTime;
		this.isSignUp = isSignUp;
	}

	public String getUuid() {
		return uuid;
	}

	public String getEmail() {
		return email;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public boolean isSignUp() {
		return isSignUp;
	}

}

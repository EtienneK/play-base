package etk.core.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import securesocial.core.PasswordInfo;

@Entity
public class PasswordInformation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String hasher;

	@Column(nullable = false)
	private String password;

	@Column
	private String salt;

	private PasswordInformation(String hasher, String password, String salt) {
		super();
		this.hasher = hasher;
		this.password = password;
		this.salt = salt;
	}

	public static PasswordInformation createFrom(PasswordInfo passwordInfo) {
		if (passwordInfo == null) {
			return null;
		}

		String salt = passwordInfo.salt().isEmpty() ? null : passwordInfo.salt().get();

		return new PasswordInformation(passwordInfo.hasher(), passwordInfo.password(), salt);
	}

	public String getHasher() {
		return hasher;
	}

	public String getPassword() {
		return password;
	}

	public String getSalt() {
		return salt;
	}

}

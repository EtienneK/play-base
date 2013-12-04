package etk.core.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import securesocial.core.OAuth1Info;

@Entity
public class OAuth1Information {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String token;

	@Column(nullable = false)
	private String secret;

	private OAuth1Information(String token, String secret) {
		this.token = token;
		this.secret = secret;
	}

	public static OAuth1Information createFrom(OAuth1Info oAuth1Info) {
		if (oAuth1Info == null) {
			return null;
		}

		return new OAuth1Information(oAuth1Info.token(), oAuth1Info.secret());
	}

	public String getToken() {
		return token;
	}

	public String getSecret() {
		return secret;
	}

}

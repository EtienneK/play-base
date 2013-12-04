package etk.core.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import securesocial.core.OAuth2Info;

@Entity
public class OAuth2Information {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String accessToken;

	@Column
	private String tokenType;

	@Column
	private Integer expiresIn;

	@Column
	private String refreshToken;

	private OAuth2Information(String accessToken, String tokenType, Integer expiresIn, String refreshToken) {
		this.accessToken = accessToken;
		this.tokenType = tokenType;
		this.expiresIn = expiresIn;
		this.refreshToken = refreshToken;
	}

	public static OAuth2Information createFrom(OAuth2Info oAuth2Info) {
		if (oAuth2Info == null) {
			return null;
		}

		String tokenType = oAuth2Info.tokenType().isEmpty() ? null : oAuth2Info.tokenType().get();
		Integer expiresIn = oAuth2Info.expiresIn().isEmpty() ? null : (Integer) oAuth2Info.expiresIn().get();
		String refreshToken = oAuth2Info.refreshToken().isEmpty() ? null : oAuth2Info.refreshToken().get();

		return new OAuth2Information(oAuth2Info.accessToken(), tokenType, expiresIn, refreshToken);
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public Integer getExpiresIn() {
		return expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

}

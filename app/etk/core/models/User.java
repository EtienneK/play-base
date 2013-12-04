package etk.core.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import scala.Option;
import securesocial.core.Identity;
import securesocial.core.IdentityId;
import securesocial.core.OAuth1Info;
import securesocial.core.OAuth2Info;
import securesocial.core.PasswordInfo;
import etk.core.enums.AuthenticationMethod;

@Entity
public class User implements Identity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String userId;

	@Column(nullable = false)
	private String providerId;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false)
	private String fullName;

	@Column
	private String email;

	@Column
	private String avatarUrl;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AuthenticationMethod authenticationMethod;

	@JoinColumn
	@OneToOne(cascade = CascadeType.ALL)
	private OAuth1Information oAuth1Information;

	@JoinColumn
	@OneToOne(cascade = CascadeType.ALL)
	private OAuth2Information oAuth2Information;

	@JoinColumn
	@OneToOne(cascade = CascadeType.ALL)
	private PasswordInformation passwordInformation;

	private User(String userId, String providerId, String firstName, String lastName, String fullName, String email,
			String avatarUrl, AuthenticationMethod authenticationMethod, OAuth1Information oAuth1Information,
			OAuth2Information oAuth2Information, PasswordInformation passwordInformation) {
		this.userId = userId;
		this.providerId = providerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.fullName = fullName;
		this.email = email;
		this.avatarUrl = avatarUrl;
		this.authenticationMethod = authenticationMethod;
		this.oAuth1Information = oAuth1Information;
		this.oAuth2Information = oAuth2Information;
		this.passwordInformation = passwordInformation;
	}

	public static User createFrom(Identity identity) {
		if (identity == null) {
			return null;
		}

		String email = identity.email().isEmpty() ? null : identity.email().get();
		String avatarUrl = identity.avatarUrl().isEmpty() ? null : identity.avatarUrl().get();
		OAuth1Info oAuth1Info = identity.oAuth1Info().isEmpty() ? null : identity.oAuth1Info().get();
		OAuth2Info oAuth2Info = identity.oAuth1Info().isEmpty() ? null : identity.oAuth2Info().get();
		PasswordInfo passwordInfo = identity.passwordInfo().isEmpty() ? null : identity.passwordInfo().get();

		return new User(identity.identityId().userId(), identity.identityId().providerId(), identity.firstName(),
				identity.lastName(), identity.fullName(), email, avatarUrl, AuthenticationMethod.valueOf(identity
						.authMethod().method()), OAuth1Information.createFrom(oAuth1Info),
				OAuth2Information.createFrom(oAuth2Info), PasswordInformation.createFrom(passwordInfo));
	}

	@Override
	public securesocial.core.AuthenticationMethod authMethod() {
		return new securesocial.core.AuthenticationMethod(authenticationMethod.name());
	}

	@Override
	public Option<String> avatarUrl() {
		return Option.apply(avatarUrl);
	}

	@Override
	public Option<String> email() {
		return Option.apply(email);
	}

	@Override
	public String firstName() {
		return firstName;
	}

	@Override
	public String fullName() {
		return fullName;
	}

	@Override
	public IdentityId identityId() {
		return new IdentityId(userId, providerId);
	}

	@Override
	public String lastName() {
		return lastName;
	}

	@Override
	public Option<OAuth1Info> oAuth1Info() {
		if (oAuth1Information == null) {
			return Option.apply(null);
		}

		return Option.apply(new OAuth1Info(oAuth1Information.getToken(), oAuth1Information.getSecret()));
	}

	@Override
	public Option<OAuth2Info> oAuth2Info() {
		if (oAuth2Information == null) {
			return Option.apply(null);
		}

		return Option.apply(new OAuth2Info(oAuth2Information.getAccessToken(), Option.apply(oAuth2Information
				.getTokenType()), Option.apply((Object) oAuth2Information.getExpiresIn()), Option
				.apply(oAuth2Information.getRefreshToken())));
	}

	@Override
	public Option<PasswordInfo> passwordInfo() {
		if (passwordInformation == null) {
			return Option.apply(null);
		}

		return Option.apply(new PasswordInfo(passwordInformation.getHasher(), passwordInformation.getPassword(), Option
				.apply(passwordInformation.getSalt())));
	}
}
